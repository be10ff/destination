#include <jni.h>

#include <gdal.h> 
#include <ogrsf_frmts.h>
#include <ogr_geometry.h>
#include <ogr_spatialref.h>

//#include "temp.h"

#include <android/log.h>
char charbuf[100];


// Function verified, do not modify
static void cp1251_to_utf8(char *out, const char *in)
{
  static const int table[128] =
    {
        0x82D0, 0x83D0, 0x9A80E2, 0x93D1, 0x9E80E2, 0xA680E2, 0xA080E2,
        0xA180E2,
        0xAC82E2, 0xB080E2, 0x89D0, 0xB980E2, 0x8AD0, 0x8CD0, 0x8BD0, 0x8FD0,
        0x92D1, 0x9880E2, 0x9980E2, 0x9C80E2, 0x9D80E2, 0xA280E2, 0x9380E2,
        0x9480E2,
        0, 0xA284E2, 0x99D1, 0xBA80E2, 0x9AD1, 0x9CD1, 0x9BD1, 0x9FD1,
        0xA0C2, 0x8ED0, 0x9ED1, 0x88D0, 0xA4C2, 0x90D2, 0xA6C2, 0xA7C2,
        0x81D0, 0xA9C2, 0x84D0, 0xABC2, 0xACC2, 0xADC2, 0xAEC2, 0x87D0,
        0xB0C2, 0xB1C2, 0x86D0, 0x96D1, 0x91D2, 0xB5C2, 0xB6C2, 0xB7C2,
        0x91D1, 0x9684E2, 0x94D1, 0xBBC2, 0x98D1, 0x85D0, 0x95D1, 0x97D1,
        0x90D0, 0x91D0, 0x92D0, 0x93D0, 0x94D0, 0x95D0, 0x96D0, 0x97D0,
        0x98D0, 0x99D0, 0x9AD0, 0x9BD0, 0x9CD0, 0x9DD0, 0x9ED0, 0x9FD0,
        0xA0D0, 0xA1D0, 0xA2D0, 0xA3D0, 0xA4D0, 0xA5D0, 0xA6D0, 0xA7D0,
        0xA8D0, 0xA9D0, 0xAAD0, 0xABD0, 0xACD0, 0xADD0, 0xAED0, 0xAFD0,
        0xB0D0, 0xB1D0, 0xB2D0, 0xB3D0, 0xB4D0, 0xB5D0, 0xB6D0, 0xB7D0,
        0xB8D0, 0xB9D0, 0xBAD0, 0xBBD0, 0xBCD0, 0xBDD0, 0xBED0, 0xBFD0,
        0x80D1, 0x81D1, 0x82D1, 0x83D1, 0x84D1, 0x85D1, 0x86D1, 0x87D1,
        0x88D1, 0x89D1, 0x8AD1, 0x8BD1, 0x8CD1, 0x8DD1, 0x8ED1, 0x8FD1
    };

  while (*in)
    if (*in & 0x80)
    {
      int v = table[(int) (0x7f & *in++)];
      if (!v)
        continue;
      *out++ = (char) v;
      *out++ = (char) (v >> 8);
      if (v >>= 16)
        *out++ = (char) v;
    }
    else
      *out++ = *in++;
  *out = 0;
}

static int ISO_8859_1_to_UTF(char *out, const char *in)
{

	while (*in)
	    if (*in<128) *out++=*in++;
	    else *out++=0xc2+(*in>0xbf), *out++=(*in++&0x3f)+0x80;
}

static int cp1252_to_UTF(char *utf8str, const char *mbstr)
{

   int res = 0;

   // loop until we reach the end of the mb string
   for (; *mbstr != '\0'; ++mbstr)
   {
     // the character needs no mapping if the highest bit is not set
     if ((*mbstr & 0x80) == 0)
     {
       if (utf8str != 0)
       {
         *utf8str++ = *mbstr;
       }
       ++res;
     }
     // otherwise mapping is necessary
     else
     {
       if (utf8str != 0)
       {
         *utf8str++ = (0xC0 | (0x03 & (*mbstr >> 6)));
         *utf8str++ = (0x80 | (0x3F & *mbstr));
       }
       res += 2;
     }
   }
   // add the terminating null character
   if (utf8str != 0)
   {
     // be sure there is enough space left in the destination buffer

     *utf8str = 0;
   }
   return res;
}

struct class_renderer
{
  jclass class_;
  //jfieldID path_id;
  jfieldID canvas_id;
  jfieldID style_id;
  jfieldID add_styles_id;
};

struct class_path
{
  jclass class_;
  jmethodID reset;
  jmethodID moveTo;
  jmethodID lineTo;
  jmethodID closePolygon;
  jmethodID rewind;
  jmethodID path;
};

struct class_canvas
{
  jclass class_;
  jmethodID drawPath;
  jmethodID drawLine;
  jmethodID drawCircle;
  jmethodID drawText;
  jmethodID drawBitmap;
};

struct class_bounds
{
  jclass class_;
  jfieldID left_id;
  jfieldID bottom_id;
  jfieldID right_id;
  jfieldID top_id;
};

struct class_array_list
{
  jclass class_;
  jmethodID toArray;
  jmethodID size;

};

struct class_vstyle
{
  jclass   class_;
  jfieldID paint_pen;
  jfieldID paint_brush;
  jfieldID filter_id;
  jfieldID image_id;
};

struct class_filter
{
  jclass class_;
  jclass function_filter_;
  jmethodID check;
  jmethodID test_value;
  jfieldID semantic_id;
};

struct class_lonlat
{
  jclass class_;
  jfieldID lon_id;
  jfieldID lat_id;
};

struct class_encoding
{
  jclass class_;
  jmethodID decode;
  jmethodID decode_with;
};

struct class_labelsmap
{
  jclass class_;
  jmethodID put;
};

struct class_pointf
{
  jclass class_;
  jmethodID pointf;
};
//-------------------      ARAB   ----------------

struct class_GIShape
{
  jclass class_;
  jmethodID shape;
  jmethodID add_pointf;
};

struct class_GITile
{
  jclass class_;
  jmethodID tile;
  jmethodID add_pointf;
};

struct class_GITileLayer
{
  jclass class_;
  //jmethodID tile_layer;
  jmethodID add_tile;
};

struct class_GIGeometryPolygon
{
  jclass class_;
  jmethodID polygon;
  jmethodID add_pointf;
  jmethodID add_interior_ring;
  //jmethodID add_interior_ring_point;
};
struct class_GIGeometryLine
{
  jclass class_;
  jmethodID line;
  jmethodID add_pointf;
  //jmethodID add_interior_ring_point;
};
struct class_GILabeledLayer
{
  jclass class_;
  jmethodID labeled_layer;
  jmethodID add_label;
};

struct warehouse
{
  class_renderer      			renderer;
  class_path          			path;
  class_canvas        			canvas;
  class_bounds        			bounds;
  class_array_list    			array_list;
  class_vstyle        			vstyle;
  class_filter        			filter;
  class_lonlat        			lonlat;
  class_encoding      			encoding;
  class_labelsmap     			map;
  class_pointf        			pointf;
  class_GIShape       			shape;
  class_GIGeometryPolygon       polygon;
  class_GIGeometryLine			line;
  class_GILabeledLayer        	labeled_layer;
  class_GITile					tile;
  class_GITileLayer        		tile_layer;
} wh;


JNIEXPORT jint
JNI_OnLoad(JavaVM *vm, void *reserved)
{
  JNIEnv *env = NULL;

  if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK)
    return -1;

  GDALAllRegister();
  OGRRegisterAll();

  // Renderer
  jclass renderer_local = env->FindClass("ru/tcgeo/gilib/GIVectorRenderer");
  wh.renderer.class_ = reinterpret_cast<jclass>(
                              env->NewGlobalRef(renderer_local));
 // wh.renderer.path_id =       env->GetFieldID(wh.renderer.class_,
 //                                             "m_path",
 //                                             "Landroid/graphics/Path;");
  wh.renderer.canvas_id =     env->GetFieldID(wh.renderer.class_,
                                              "m_canvas",
                                              "Landroid/graphics/Canvas;");
  wh.renderer.style_id =      env->GetFieldID(wh.renderer.class_,
                                              "m_style",
                                              "Lru/tcgeo/gilib/GIVectorStyle;");
  wh.renderer.add_styles_id = env->GetFieldID(wh.renderer.class_,
                                              "m_additional_styles",
                                              "Ljava/util/ArrayList;");

  // Path
  jclass path_local = env->FindClass("android/graphics/Path");
  wh.path.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(path_local));
  wh.path.reset =         env->GetMethodID(wh.path.class_, "reset", "()V");
  wh.path.moveTo =        env->GetMethodID(wh.path.class_, "moveTo", "(FF)V");
  wh.path.lineTo =        env->GetMethodID(wh.path.class_, "lineTo", "(FF)V");
  wh.path.closePolygon =  env->GetMethodID(wh.path.class_, "close", "()V");
  wh.path.rewind =        env->GetMethodID(wh.path.class_, "rewind", "()V");
  wh.path.path = 		  env->GetMethodID(wh.path.class_, "<init>", "()V");

  // Canvas
  jclass canvas_local = env->FindClass("android/graphics/Canvas");
  wh.canvas.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(canvas_local));
  wh.canvas.drawPath =  env->GetMethodID(wh.canvas.class_, "drawPath",
                          "(Landroid/graphics/Path;Landroid/graphics/Paint;)V");
  wh.canvas.drawLine =  env->GetMethodID(wh.canvas.class_, "drawLine",
                          "(FFFFLandroid/graphics/Paint;)V");
  wh.canvas.drawCircle = env->GetMethodID(wh.canvas.class_, "drawCircle",
                          "(FFFLandroid/graphics/Paint;)V");
  wh.canvas.drawText =  env->GetMethodID(wh.canvas.class_, "drawText",
                           "(Ljava/lang/String;FFLandroid/graphics/Paint;)V");
  wh.canvas.drawBitmap = env->GetMethodID(wh.canvas.class_, "drawBitmap",
                           "(Landroid/graphics/Bitmap;FFLandroid/graphics/Paint;)V");

  // GIBounds
  jclass bounds_local = env->FindClass("ru/tcgeo/gilib/GIBounds");
  wh.bounds.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(bounds_local));
  wh.bounds.left_id =   env->GetFieldID(wh.bounds.class_, "m_left", "D");
  wh.bounds.bottom_id = env->GetFieldID(wh.bounds.class_, "m_bottom", "D");
  wh.bounds.right_id =  env->GetFieldID(wh.bounds.class_, "m_right", "D");
  wh.bounds.top_id =    env->GetFieldID(wh.bounds.class_, "m_top", "D");

  // vStyle
  jclass vstyle_local = env->FindClass("ru/tcgeo/gilib/GIVectorStyle");
  wh.vstyle.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(vstyle_local));
  wh.vstyle.paint_pen =  env->GetFieldID(wh.vstyle.class_, "m_paint_pen",
                                       "Landroid/graphics/Paint;");
  wh.vstyle.paint_brush =  env->GetFieldID(wh.vstyle.class_, "m_paint_brush",
                                       "Landroid/graphics/Paint;");
  wh.vstyle.filter_id = env->GetFieldID(wh.vstyle.class_, "m_filter",
                                       "Lru/tcgeo/gilib/GIFilter;");
  wh.vstyle.image_id = env->GetFieldID(wh.vstyle.class_, "m_image",
                                       "Landroid/graphics/Bitmap;");


  // Array_list
  jclass array_list_local = env->FindClass("java/util/ArrayList");
  wh.array_list.class_ = reinterpret_cast<jclass>(
                                      env->NewGlobalRef(array_list_local));
  wh.array_list.toArray = env->GetMethodID(wh.array_list.class_, "toArray",
                                           "()[Ljava/lang/Object;");
  wh.array_list.size =    env->GetMethodID(wh.array_list.class_, "size",
                                           "()I");

  // Filters
  jclass filter_local = env->FindClass("ru/tcgeo/gilib/GIFilter");
  jclass function_filter_local = env->FindClass("ru/tcgeo/gilib/GIFunctionFilter");
  wh.filter.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(filter_local));
  wh.filter.function_filter_ = reinterpret_cast<jclass>(
                                env->NewGlobalRef(function_filter_local));
  wh.filter.check =       env->GetMethodID(wh.filter.class_,
                                           "Check",
                                           "()Z");
  wh.filter.test_value =  env->GetMethodID(wh.filter.function_filter_,
                                           "TestValue",
                                           "(Ljava/lang/String;)Z");
  wh.filter.semantic_id = env->GetFieldID(wh.filter.function_filter_,
                                          "m_semantic",
                                          "I");

  // LonLat
  jclass lonlat_local = env->FindClass("ru/tcgeo/gilib/GILonLat");
  wh.lonlat.class_ =   reinterpret_cast<jclass>(env->NewGlobalRef(lonlat_local));
  wh.lonlat.lon_id =    env->GetFieldID(wh.lonlat.class_, "m_lon", "D");
  wh.lonlat.lat_id =    env->GetFieldID(wh.lonlat.class_, "m_lat", "D");

  // Encoding
  jclass encoding_local =   env->FindClass("ru/tcgeo/gilib/GIEncoding");
  wh.encoding.class_ =   reinterpret_cast<jclass>(env->NewGlobalRef(encoding_local));
  wh.encoding.decode =      env->GetMethodID(wh.encoding.class_,
                                             "decode",
                                             "([B)Ljava/lang/String;");
  wh.encoding.decode_with = env->GetStaticMethodID(wh.encoding.class_,
                                                   "decode",
                                                   "([BLjava/lang/String;)Ljava/lang/String;");

  // Map
  jclass local_map = env->FindClass("ru/tcgeo/gilib/LabelsMap");
  wh.map.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(local_map));
  wh.map.put =    env->GetMethodID(wh.map.class_,
      "put",
      "(Landroid/graphics/PointF;Ljava/lang/String;)V");

  // PointF
  jclass pointf_local = env->FindClass("android/graphics/PointF");
  wh.pointf.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(pointf_local));
  wh.pointf.pointf = env->GetMethodID(wh.pointf.class_, "<init>", "(FF)V");


  // shape;
  jclass shape = env->FindClass("ru/tcgeo/gilib/planimetry/GIShape");
  wh.shape.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(shape));
  wh.shape.shape = env->GetMethodID(wh.shape.class_, "<init>", "(Ljava/lang/String;)V");
  wh.shape.add_pointf =    env->GetMethodID(wh.shape.class_, "add", "(Landroid/graphics/PointF;)V");
  //polygon
  jclass polygon = env->FindClass("ru/tcgeo/gilib/planimetry/GIGeometryPolygon");
  wh.polygon.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(polygon));
  wh.polygon.polygon = env->GetMethodID(wh.polygon.class_, "<init>", "(Ljava/lang/String;)V");
  wh.polygon.add_pointf =    env->GetMethodID(wh.polygon.class_, "add", "(Landroid/graphics/PointF;)V");
  wh.polygon.add_interior_ring =    env->GetMethodID(wh.polygon.class_, "addRing", "(Lru/tcgeo/gilib/planimetry/GIGeometryPolygon;)V");
  //line
  jclass line = env->FindClass("ru/tcgeo/gilib/planimetry/GIGeometryLine");
  wh.line.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(line));
  wh.line.line = env->GetMethodID(wh.line.class_, "<init>", "(Ljava/lang/String;)V");
  wh.line.add_pointf =    env->GetMethodID(wh.line.class_, "add", "(Landroid/graphics/PointF;)V");
  // labels;
  jclass labeled_layer = env->FindClass("ru/tcgeo/gilib/planimetry/GILabeledLayer");
  wh.labeled_layer.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(labeled_layer));
  wh.labeled_layer.labeled_layer = env->GetMethodID(wh.labeled_layer.class_, "<init>", "()V");
  wh.labeled_layer.add_label =    env->GetMethodID(wh.labeled_layer.class_, "add", "(Lru/tcgeo/gilib/planimetry/GIShape;)V");

  // tile;
  jclass tile = env->FindClass("ru/tcgeo/gilib/GITile");
  wh.tile.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(tile));
  wh.tile.tile = env->GetMethodID(wh.tile.class_, "<init>", "(Ljava/lang/String;FF)V");
  wh.tile.add_pointf =    env->GetMethodID(wh.tile.class_, "add", "(Landroid/graphics/PointF;)V");
  // tiles;
  jclass tile_layer = env->FindClass("ru/tcgeo/gilib/GITileLayer");
  wh.tile_layer.class_ = reinterpret_cast<jclass>(env->NewGlobalRef(tile_layer));
  wh.tile_layer.add_tile =    env->GetMethodID(wh.tile_layer.class_, "add", "(Lru/tcgeo/gilib/GITile;)V");

  env->DeleteLocalRef(renderer_local);
  env->DeleteLocalRef(path_local);
  env->DeleteLocalRef(canvas_local);
  env->DeleteLocalRef(bounds_local);
  env->DeleteLocalRef(vstyle_local);
  env->DeleteLocalRef(array_list_local);
  env->DeleteLocalRef(filter_local);
  env->DeleteLocalRef(function_filter_local);
  env->DeleteLocalRef(lonlat_local);
  env->DeleteLocalRef(encoding_local);
  env->DeleteLocalRef(pointf_local);

  env->DeleteLocalRef(shape);
  env->DeleteLocalRef(polygon);
  env->DeleteLocalRef(line);
  env->DeleteLocalRef(labeled_layer);

  env->DeleteLocalRef(tile);
  env->DeleteLocalRef(tile_layer);

  return JNI_VERSION_1_6;
}

extern "C"
{

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_initVectorLayer(JNIEnv* env, jobject thiz,
    jstring path);

//TODO
JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GITileLayer_initTileLayer(JNIEnv* env, jobject thiz,
    jstring path);

JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GITileRenderer_drawLayer(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jstring symantic,
    jstring conditions,
    jlong bitmapWidth,
    jlong bitmapHeight,
    jobject area,
    jobject labels_array);

JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_drawLayer(JNIEnv* env, jobject thiz,
    jlong layer_id, jint bitmapWidth, jint bitmapHeight, jobject area, jfloat scale);


JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_drawText(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jint bitmapWidth,
    jint bitmapHeight,
    jobject area,
    jobject encoding);


JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_getText(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jint bitmapWidth,
    jint bitmapHeight,
    jobject area,
    jobject encoding,
    jobject lables);

/*JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_getStr(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jstring symantic,
    jlong bitmapWidth,
    jlong bitmapHeight,
    jobject area,
    jobject encoding,
    jobject labels_array);*/

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_getLabel(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jstring symantic,
    jstring conditions,
    jlong bitmapWidth,
    jlong bitmapHeight,
    jobject area,
    jobject encoding,
    jobject labels_array);

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_getLayerType(JNIEnv* env,
    jobject thiz,
    jlong layer_id);
/*
JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_getLabels(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jstring symantic,
    jint bitmapWidth,
    jint bitmapHeight,
    jobject area,
    jobject encoding);
*/
JNIEXPORT jboolean
JNICALL
Java_ru_tcgeo_gilib_GIFunctionFilter_Check(JNIEnv* env, jobject thiz);


JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIProjection_initProjection(JNIEnv* env, jobject thiz,
  jstring wkt_description);

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_getProjection(JNIEnv* env, jobject thiz, jlong layer_id);

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GITileLayer_getTileProjection(JNIEnv* env, jobject thiz, jlong layer_id);

JNIEXPORT jdoubleArray
JNICALL
Java_ru_tcgeo_gilib_GIProjection_reprojectPoint(JNIEnv* env, jobject thiz,
  jobject point, jlong source_id, jlong dest_id);

//ARAB
JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIMap_GetProjectionInfo(JNIEnv* env, jobject thiz, jlong source_id);

JNIEXPORT jdouble
JNICALL
Java_ru_tcgeo_gilib_GIMap_GetGeometryLength(JNIEnv* env, jobject thiz, jobject geometry);

/*JNIEXPORT jdouble
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetGeometryArea(JNIEnv* env, jobject thiz, jlong layer_id, jobject geometry);*/

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFeatureCountInArea(JNIEnv* env, jobject thiz, jlong layer_id, jobject area);

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFieldsCount(JNIEnv* env, jobject thiz, jlong layer_id);

JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFieldName(JNIEnv* env, jobject thiz, jlong layer_id, jlong field_id);

JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFieldType(JNIEnv* env, jobject thiz, jlong layer_id, jlong field_id);


JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFieldAsString(JNIEnv* env, jobject thiz, jlong layer_id, jlong field_id, jobject area, jlong feature_id);

/*JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_RequestDataIn(JNIEnv* env, jobject thiz, jlong layer_id, jobject area);*/

JNIEXPORT jboolean
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_RemoveLayer(JNIEnv* env, jobject thiz, jlong layer_id);

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIProjection_initProjectionFromEPSG(JNIEnv* env, jobject thiz, jlong EPSG_id);

JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_GetFieldIndex(JNIEnv* env, jobject thiz, jlong layer_id, jstring name);

JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIEditableRenderer_drawFeature(JNIEnv* env, jobject thiz,
		jstring wkt, jint bitmapWidth, jint bitmapHeight, jobject area, jfloat scale);
}

JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIMap_GetProjectionInfo(JNIEnv* env, jobject thiz, jlong source_id)
{
	OGRSpatialReference* source = reinterpret_cast<OGRSpatialReference*>(source_id);
	char* pszWKT = NULL;
	source->exportToWkt(&pszWKT);

	return env->NewStringUTF(pszWKT);
}

JNIEXPORT jdouble
JNICALL
Java_ru_tcgeo_gilib_GIMap_GetGeometryLength(JNIEnv* env, jobject thiz, jobject geometry)
{
	OGRLinearRing* ring = new OGRLinearRing();

	int points_num = env->CallIntMethod(geometry, wh.array_list.size);
	jobjectArray points_array = (jobjectArray) env->CallObjectMethod(geometry, wh.array_list.toArray);
	for(int i = 0; i < points_num; i++)
	{
		jobject point = env->GetObjectArrayElement(points_array, i);
		double lon = env->GetDoubleField(point, wh.lonlat.lon_id);
		double lat = env->GetDoubleField(point, wh.lonlat.lat_id);

    	ring->addPoint(lon, lat);
	}
	OGRCurve* lstring = ring;
	return lstring->get_Length();
}

/*JNIEXPORT jdouble
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetGeometryArea(JNIEnv* env, jobject thiz, jlong layer_id, jobject geometry)
{
	return 0;
}*/


JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFeatureCountInArea(JNIEnv* env, jobject thiz, jlong layer_id, jobject area)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRFeature* feature = NULL;

	double left =   env->GetDoubleField(area, wh.bounds.left_id);
	double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
	double right =  env->GetDoubleField(area, wh.bounds.right_id);
	double top =    env->GetDoubleField(area, wh.bounds.top_id);
	layer->SetSpatialFilterRect(left, bottom, right, top);
	long res = layer->GetFeatureCount();
	return res;
}

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFieldsCount(JNIEnv* env, jobject thiz, jlong layer_id)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRFeatureDefn* layerDF = layer->GetLayerDefn();
	long res = layerDF->GetFieldCount();
	return res;
}

JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFieldName(JNIEnv* env, jobject thiz, jlong layer_id, jlong field_id)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRFeatureDefn* layerDF = layer->GetLayerDefn();
	//char res[64];
	if( 0 <= field_id <= layerDF->GetFieldCount())
	{
		OGRFieldDefn* fieldDF = layerDF->GetFieldDefn(field_id);
		const char* field_name = fieldDF->GetNameRef(); //GetName() ??
		return env->NewStringUTF(field_name);
	}
	//return env->NewStringUTF("");
	return NULL;
}

JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFieldType(JNIEnv* env, jobject thiz, jlong layer_id, jlong field_id)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRFeatureDefn* featureDF = layer->GetLayerDefn();
	//char res[64];
	if( 0 <= field_id <= featureDF->GetFieldCount())
	{
		OGRFieldDefn* fieldDF = featureDF->GetFieldDefn(field_id);
		OGRFieldType field_type = fieldDF->GetType();
		const char* field_type_name = fieldDF->GetFieldTypeName(field_type);
		return env->NewStringUTF(field_type_name);
	}
	return NULL;
}

JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_GetFieldAsString(JNIEnv* env, jobject thiz, jlong layer_id, jlong field_id, jobject area, jlong feature_id)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRFeatureDefn* layerDF = layer->GetLayerDefn();
	if((field_id < 0) || (field_id >= layerDF->GetFieldCount()))
	{
		return NULL;
	}

	double left =   env->GetDoubleField(area, wh.bounds.left_id);
	double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
	double right =  env->GetDoubleField(area, wh.bounds.right_id);
	double top =    env->GetDoubleField(area, wh.bounds.top_id);
	layer->SetSpatialFilterRect(left, bottom, right, top);

	layer->ResetReading();
	int count = 0;
	OGRFeature* feature = layer->GetNextFeature();
	while(feature != NULL && count != feature_id)
	{
		feature = layer->GetNextFeature();
		count++;
	}

	OGRFieldDefn* fieldDF = layerDF->GetFieldDefn(field_id);
	if(fieldDF->GetType() == OFTString)
	{
		int  i = 0;
	}
	const char* field_value = feature->GetFieldAsString(field_id);
	char* utf_text = new char[strlen(field_value) * 2];
	cp1251_to_utf8(utf_text, field_value);
	return env->NewStringUTF(utf_text);
}

/*JNIEXPORT jstring
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_RequestDataIn(JNIEnv* env, jobject thiz, jlong layer_id, jobject area)
{
	//
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRFeature* feature = NULL;

	long id = layer_id;
	char buf[128];

	long lRes = 0;
	double left =   env->GetDoubleField(area, wh.bounds.left_id);
	double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
	double right =  env->GetDoubleField(area, wh.bounds.right_id);
	double top =    env->GetDoubleField(area, wh.bounds.top_id);

	layer->SetSpatialFilterRect(left, bottom, right, top);

	OGRFeatureDefn* layerDF = layer->GetLayerDefn();

	for(int i = 0; i < layer->GetFeatureCount(); i++)
	{

		OGRFeature* feature = 	layer->GetFeature(i);

		for(int j = 0; j < layerDF->GetFieldCount(); j++)
		{
			OGRFieldDefn* fieldDF = layerDF->GetFieldDefn(j);
			const char* field_name = fieldDF->GetNameRef();
			const char* field_value = feature->GetFieldAsString(j);
		}
	}
	sprintf(buf, "res %f", left);

	return env->NewStringUTF(buf);
}
*/
OGRDataSource* data_source;
JNIEXPORT jboolean
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_RemoveLayer(JNIEnv* env, jobject thiz, jlong layer_id)
{
	 OGRDataSource::DestroyDataSource(data_source);
	return true;
}

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIProjection_initProjectionFromEPSG(JNIEnv* env, jobject thiz,
  jlong EPSG_id)
{
  std::string epsg = "EPSG:" + EPSG_id;
  OGRSpatialReference* srs = new OGRSpatialReference();
  srs->SetWellKnownGeogCS("EPSG:6872");
  return reinterpret_cast<long>(srs);
}
// end ARAB

OGRFeature* current_feature;

JNIEXPORT jboolean
JNICALL
Java_ru_tcgeo_gilib_GIFunctionFilter_Check(JNIEnv* env, jobject thiz)
{
  int semantic = (int)env->GetIntField(thiz, wh.filter.semantic_id);
  const char* text = current_feature->GetFieldAsString(semantic);
  char* utf_text = new char[strlen(text) * 2];
  // TODO: Refactor to use encoding
  cp1251_to_utf8(utf_text, text);
  jstring value = env->NewStringUTF(utf_text);

  return env->CallBooleanMethod(thiz, wh.filter.test_value, value);
}

/*JNIEXPORT jboolean
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_RequestDataIn(JNIEnv* env, jobject thiz)
{
	return true;
}*/

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_getProjection(JNIEnv* env, jobject thiz, jlong layer_id)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRSpatialReference* dst = layer->GetSpatialRef();
	//OGRSpatialReference* dst = new OGRSpatialReference(description);
	return reinterpret_cast<long>(dst);
}

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GITileLayer_getTileProjection(JNIEnv* env, jobject thiz, jlong layer_id)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRSpatialReference* dst = layer->GetSpatialRef();
	//OGRSpatialReference* dst = new OGRSpatialReference(description);
	return reinterpret_cast<long>(dst);
}

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIProjection_initProjection(JNIEnv* env, jobject thiz,
  jstring wkt_description)
{
  //jboolean isCopy;
  const char* description = env->GetStringUTFChars(wkt_description, 0); //&isCopy
  OGRSpatialReference* srs = new OGRSpatialReference(description);
  env->ReleaseStringUTFChars(wkt_description, description);

  return reinterpret_cast<long>(srs);
}


JNIEXPORT jdoubleArray
JNICALL
Java_ru_tcgeo_gilib_GIProjection_reprojectPoint(JNIEnv* env, jobject thiz,
  jobject point, jlong source_id, jlong dest_id)
{
  double buf[2];
  buf[0] = env->GetDoubleField(point, wh.lonlat.lon_id);
  buf[1] = env->GetDoubleField(point, wh.lonlat.lat_id);


  if ((0 != source_id) and (0 != dest_id) and (source_id != dest_id))
  {
    OGRCoordinateTransformation* transformation;
    OGRSpatialReference* source = reinterpret_cast<OGRSpatialReference*>(source_id);
    OGRSpatialReference* dest =   reinterpret_cast<OGRSpatialReference*>(dest_id);

    // TODO: if  = 0?
    transformation = OGRCreateCoordinateTransformation(source, dest);

    transformation->Transform(1, &buf[0], &buf[1]);
    OGRCoordinateTransformation::DestroyCT(transformation);
  }

  jdoubleArray result = env->NewDoubleArray(2);
  env->SetDoubleArrayRegion(result, 0, 2, buf);
  return result;
}



JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorLayer_initVectorLayer(JNIEnv* env, jobject thiz,
    jstring path)
{
  const char* path_ = env->GetStringUTFChars(path, 0);
  //OGRDataSource*
  data_source = OGRSFDriverRegistrar::Open(path_, false);
  env->ReleaseStringUTFChars(path, path_);

  if (NULL == data_source)
    return 0;

  OGRLayer* layer = data_source->GetLayer(0);
  current_feature = 0;

  return reinterpret_cast<long>(layer);
}
//TODO
JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GITileLayer_initTileLayer(JNIEnv* env, jobject thiz,
    jstring path)
{
  const char* path_ = env->GetStringUTFChars(path, 0);
  data_source = OGRSFDriverRegistrar::Open(path_, false);
  env->ReleaseStringUTFChars(path, path_);

  if (NULL == data_source)
    return 0;

  OGRLayer* layer = data_source->GetLayer(0);
  current_feature = 0;

  return reinterpret_cast<long>(layer);
}

JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GITileRenderer_drawLayer(JNIEnv* env,
	    jobject thiz,
	    jlong layer_id,
	    jstring symantic,
	    jstring conditions,
	    jlong bitmapWidth,
	    jlong bitmapHeight,
	    jobject area,
	    jobject tile_layer)
{
	double left =   env->GetDoubleField(area, wh.bounds.left_id);
	double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
	double right =  env->GetDoubleField(area, wh.bounds.right_id);
	double top =    env->GetDoubleField(area, wh.bounds.top_id);

	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);

	float koeffX = (float) (bitmapWidth / (right - left));
	float koeffY = (float) (bitmapHeight / (top - bottom));

	layer->SetSpatialFilterRect(left, bottom, right, top);
	char query[128];
	const char* symantic_string = env->GetStringUTFChars(symantic, 0);
	const char* condition_string = env->GetStringUTFChars(conditions, 0);
	sprintf(query, "(%s)", condition_string);

	layer->SetAttributeFilter(query);
	layer->ResetReading();
	int res = layer->GetFeatureCount();

	OGRwkbGeometryType layerType = layer->GetGeomType();

	if(layerType == wkbPolygon)
	{
		OGRPolygon* ogr_polygon = NULL;
		OGRPoint* point = NULL;
		OGRFeature* feature = NULL;
		while ((feature = layer->GetNextFeature()) != NULL)
		{
			const char* text = feature->GetFieldAsString(feature->GetFieldIndex(symantic_string));
			float lower = feature->GetFieldAsDouble(feature->GetFieldIndex("lower"));
			float upper = feature->GetFieldAsDouble(feature->GetFieldIndex("upper"));
			ogr_polygon = (OGRPolygon*) feature->GetGeometryRef();
			jstring str = env->NewStringUTF(text);
			if(strlen(text) > 0)
			{
				//TODO
				float x, y;
				jobject polygon = env->NewObject(wh.tile.class_, wh.tile.tile, str, upper, lower);
				OGRPoint point;
				OGRLinearRing* ring = ogr_polygon->getExteriorRing();
				for (int i = 0; i < ring->getNumPoints(); ++i)
				{

					ring->getPoint(i, &point);
					x = (point.getX() - left) * koeffX;
					y = (bitmapHeight - (point.getY() - bottom) * koeffY);
					jobject pointf = env->NewObject(wh.pointf.class_, wh.pointf.pointf, x, y);
					env->CallVoidMethod(polygon, wh.tile.add_pointf, pointf);

					env->DeleteLocalRef(pointf);
				}
				env->CallVoidMethod(tile_layer, wh.tile_layer.add_tile, polygon);
				env->DeleteLocalRef(polygon);
			}
			env->DeleteLocalRef(str);
		} // while
	}
	return 0;
}

/*
JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_drawLayer(
    JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jint bitmapWidth,
    jint bitmapHeight,
    jobject area,
    jfloat scale)
{
  //jobject path_r =        env->GetObjectField(thiz, wh.renderer.path_id);
  jobject canvas =      env->GetObjectField(thiz, wh.renderer.canvas_id);
  jobject vstyle =      env->GetObjectField(thiz, wh.renderer.style_id);
  jobject add_styles =  env->GetObjectField(thiz, wh.renderer.add_styles_id);

  int styles_num = env->CallIntMethod(add_styles, wh.array_list.size);
  jobjectArray styles_array =
    (jobjectArray) env->CallObjectMethod(add_styles, wh.array_list.toArray);

  jobject default_paint = env->GetObjectField(vstyle, wh.vstyle.paint_id);
  jobject paint = default_paint;
  jobject image = env->GetObjectField(vstyle, wh.vstyle.image_id);

  // GIBounds
  double left =   env->GetDoubleField(area, wh.bounds.left_id);
  double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
  double right =  env->GetDoubleField(area, wh.bounds.right_id);
  double top =    env->GetDoubleField(area, wh.bounds.top_id);
  //__android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "left: %f \n top %f \n right %f \n bottom %f \n width %f \n %f height" ,left, top, right, bottom, right - left, top - bottom);

  OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
  OGRFeature* feature = NULL;

  // pixel koeff
  float koeffX = (float) (bitmapWidth / (right - left));
  float koeffY = (float) (bitmapHeight / (top - bottom));

  //layer->SetSpatialFilter(NULL);

  layer->ResetReading();
  layer->SetSpatialFilterRect(left, bottom, right, top);
 // env->CallVoidMethod(path, wh.path.reset);

  OGRwkbGeometryType layerType = layer->GetGeomType();

  switch (layerType)
  {
  case wkbUnknown:
    break;
  case wkbPoint:
    break;
  case wkbLineString:
    {
    OGRLineString* line = NULL;

    while ((feature = layer->GetNextFeature()) != NULL)
    {

      current_feature = feature;
      // here we should check feature with, for example, default filter
      for(int i = 0; i < styles_num; ++i)
      {
        jobject style = env->GetObjectArrayElement(styles_array, i);
        jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);
        if(env->CallBooleanMethod(filter, wh.filter.check))
        {
          paint = env->GetObjectField(style, wh.vstyle.paint_id);
          env->DeleteLocalRef(style);
          env->DeleteLocalRef(filter);
          break;
        }
        // If nothing is applicable default paint remains

        env->DeleteLocalRef(style);
        env->DeleteLocalRef(filter);
      }

      line = (OGRLineString*) feature->GetGeometryRef();
      float x0, y0, x1, y1;
      for (int i = 1; i < line->getNumPoints(); ++i)
      {
        x0 = (line->getX(i - 1) - left) * koeffX;
        x1 = (line->getX(i) - left) * koeffX;

        y0 = bitmapHeight - (line->getY(i - 1) - bottom) * koeffY;
        y1 = bitmapHeight - (line->getY(i) - bottom) * koeffY;

        env->CallVoidMethod(canvas, wh.canvas.drawLine,
                            x0,
                            y0,
                            x1,
                            y1,
                            paint);

        if(!env->IsSameObject(paint, default_paint))
        {
          env->DeleteLocalRef(paint);
          paint = default_paint;
        }
      }
    } // while

    break;
  }
  case wkbPolygon:
    {

    OGRPolygon* polygon = NULL;
    //
    OGRGeometryCollection* multy = NULL;
    // Parse rings
    while ((feature = layer->GetNextFeature()) != NULL)
    {
    	 jobject path = env->NewObject(wh.path.class_, wh.path.path);
    	//env->CallVoidMethod(path, wh.path.reset);
      // here we should check feature with, for example, default filter
      for(int i = 0; i < styles_num; ++i)
      {
        jobject style = env->GetObjectArrayElement(styles_array, i);
        jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);

        if(env->CallBooleanMethod(filter, wh.filter.check))
        {
          paint = env->GetObjectField(style, wh.vstyle.paint_id);
          env->DeleteLocalRef(style);
          env->DeleteLocalRef(filter);
          break;
        }
        // If nothing is applicable default paint remains

        env->DeleteLocalRef(style);
        env->DeleteLocalRef(filter);
      }

      OGRPoint point;
      float x, y;
      //ARAB
      multy = (OGRGeometryCollection*)feature->GetGeometryRef();
      for(int i = 0; i < multy->getNumGeometries(); i++ )
      {
    	  OGRGeometry * pol =	multy->getGeometryRef(i);
          char* Wkb = NULL;
          pol->exportToWkt(&Wkb);
    		__android_log_print(ANDROID_LOG_VERBOSE, "NATIVE",  "%s", Wkb);
      }
      polygon = (OGRPolygon*) feature->GetGeometryRef();


      //  === Exterior ring ===
      OGRLinearRing* ring = polygon->getExteriorRing();

      // first point
      ring->getPoint(0, &point);
      x = (point.getX() - left) * koeffX;
      y = (bitmapHeight - (point.getY() - bottom) * koeffY);
      env->CallVoidMethod(path, wh.path.moveTo, x, y);
      // all other points
      for (int i = 1; i < ring->getNumPoints(); ++i)
      {
        ring->getPoint(i, &point);
        x = (point.getX() - left) * koeffX;
        y = (bitmapHeight - (point.getY() - bottom) * koeffY);
        env->CallVoidMethod(path, wh.path.lineTo, x, y);
      }
      //env->CallVoidMethod(canvas, wh.canvas.drawPath, path, paint);
      env->CallVoidMethod(path, wh.path.closePolygon);

      // === Interior rings ===
      for (int r = 0; r < polygon->getNumInteriorRings(); ++r)
      {
        ring = polygon->getInteriorRing(r);

        // first point
        ring->getPoint(0, &point);
        x = (point.getX() - left) * koeffX;
        y = (bitmapHeight - (point.getY() - bottom) * koeffY);
        env->CallVoidMethod(path, wh.path.moveTo, x, y);

        // other points
        for (int i = 1; i < ring->getNumPoints(); ++i)
        {
          ring->getPoint(i, &point);
          x = (point.getX() - left) * koeffX;
          y = (bitmapHeight - (point.getY() - bottom) * koeffY);
          env->CallVoidMethod(path, wh.path.lineTo, x, y);

        }
        env->CallVoidMethod(path, wh.path.closePolygon);
      }

      // draw Path with current feature
      env->CallVoidMethod(canvas, wh.canvas.drawPath, path, paint);
      if(!env->IsSameObject(paint, default_paint))
      {
        env->DeleteLocalRef(paint);
        paint = default_paint;
      }
      env->CallVoidMethod(path, wh.path.rewind);
      env->DeleteLocalRef(path);
    } // while

    break;

  }
  case wkbMultiPoint:
    {
    OGRMultiPoint* multi_point = NULL;
    OGRPoint* point = NULL;

    while ((feature = layer->GetNextFeature()) != NULL)
    {
      current_feature = feature;

      // here we should check feature with, for example, default filter
      for(int i = 0; i < styles_num; ++i)
      {
        jobject style = env->GetObjectArrayElement(styles_array, i);
        jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);


        if(env->CallBooleanMethod(filter, wh.filter.check))
        {
          paint = env->GetObjectField(style, wh.vstyle.paint_id);
          if (NULL == paint)
            image = env->GetObjectField(style, wh.vstyle.image_id);
          env->DeleteLocalRef(style);
          env->DeleteLocalRef(filter);
          break;
        }

        env->DeleteLocalRef(style);
        env->DeleteLocalRef(filter);

        // If nothing is applicable default paint remains
      }

      float x, y;
      multi_point = (OGRMultiPoint*) feature->GetGeometryRef();
      for (int g = 0; g < multi_point->getNumGeometries(); ++g)
      {
        point = (OGRPoint*) multi_point->getGeometryRef(g);
        x = (point->getX() - left) * koeffX;
        y = (bitmapHeight - (point->getY() - bottom) * koeffY);

        // TODO: Hard-coded metro sign position!!! Done
        if (NULL != paint)
          env->CallVoidMethod(canvas, wh.canvas.drawCircle, x, y, 8.0*scale, paint);
        else
        {
          //env->CallVoidMethod(canvas, wh.canvas.drawBitmap, image, x - 10, y - 7, NULL);
			jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
			//TODO: fatal signal 11 (sigsegv) at 0xdeadbaad
			if(bitmapClass == NULL)return -1;
			jmethodID getheight = env->GetMethodID(bitmapClass, "getHeight", "()I");
			if(getheight == NULL)return -1;
			jmethodID getwidth = env->GetMethodID(bitmapClass, "getWidth", "()I");
			if(getwidth == NULL)return -1;
			int height = env->CallIntMethod(image, getheight);
			int width = env->CallIntMethod(image, getwidth);
		    jclass rectClass = env->FindClass("android/graphics/Rect");
			if(rectClass == NULL)return -1;
			jmethodID rectInit = env->GetMethodID(rectClass,"<init>","(IIII)V");
			if(rectInit == NULL)return -1;
			jobject src = env->NewObject(rectClass, rectInit, 0, 0, width, height);
			height = height*scale;
			width = width*scale;
			jobject dst = env->NewObject(rectClass, rectInit, int(x-width/2), int(y-height/2), int(width/2 + x), int(height/2 + y));
			//jclass Canvas_class = env->FindClass("android/graphics/Canvas");
			jmethodID drawBitmap_method = env->GetMethodID(wh.canvas.class_, "drawBitmap", "(Landroid/graphics/Bitmap;Landroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Paint;)V");
			if(drawBitmap_method == NULL)return -1;
			env->CallVoidMethod(canvas, drawBitmap_method, image, src, dst, NULL);
			//env->DeleteLocalRef(src);
			//env->DeleteLocalRef(dst);

			env->DeleteLocalRef(rectClass);
			env->DeleteLocalRef(bitmapClass);
        }
      }

      if (NULL == paint)
        paint = default_paint;

      if(!env->IsSameObject(paint, default_paint))
      {
        env->DeleteLocalRef(paint);
        paint = default_paint;
      }

    }
    break;
  }
  case wkbMultiLineString:
    break;
  case wkbMultiPolygon:
    break;
  case wkbGeometryCollection:
    break;
  case wkbNone:
    break;
  case wkbLinearRing:
    break;
  case wkbPoint25D:
    break;
  case wkbLineString25D:
    break;
  case wkbPolygon25D:
    break;
  case wkbMultiPoint25D:
    break;
  case wkbMultiLineString25D:
    break;
  case wkbMultiPolygon25D:
    break;
  case wkbGeometryCollection25D:
    break;
  }

  return 0;
}
*/
JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_drawLayer(
    JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jint bitmapWidth,
    jint bitmapHeight,
    jobject area,
    jfloat scale)
{

  jobject canvas =      env->GetObjectField(thiz, wh.renderer.canvas_id);
  jobject vstyle =      env->GetObjectField(thiz, wh.renderer.style_id);
  jobject add_styles =  env->GetObjectField(thiz, wh.renderer.add_styles_id);

  int styles_num = env->CallIntMethod(add_styles, wh.array_list.size);
  jobjectArray styles_array =
    (jobjectArray) env->CallObjectMethod(add_styles, wh.array_list.toArray);

  jobject paint_pen = env->GetObjectField(vstyle, wh.vstyle.paint_pen);
  jobject paint_brush = env->GetObjectField(vstyle, wh.vstyle.paint_brush);
  //jobject paint = default_paint;
  jobject image = env->GetObjectField(vstyle, wh.vstyle.image_id);

  // GIBounds
  double left =   env->GetDoubleField(area, wh.bounds.left_id);
  double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
  double right =  env->GetDoubleField(area, wh.bounds.right_id);
  double top =    env->GetDoubleField(area, wh.bounds.top_id);

  OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
  OGRFeature* feature = NULL;

  // pixel koeff
  float koeffX = (float) (bitmapWidth / (right - left));
  float koeffY = (float) (bitmapHeight / (top - bottom));

  layer->ResetReading();
  layer->SetSpatialFilterRect(left, bottom, right, top);
 // env->CallVoidMethod(path, wh.path.reset);

  OGRwkbGeometryType layerType = layer->GetGeomType();
  while ((feature = layer->GetNextFeature()) != NULL)
  {
	  OGRGeometry* geom = feature->GetGeometryRef();
	  OGRwkbGeometryType featureType = geom->getGeometryType();

	  switch (featureType)
	  {
	  case wkbUnknown:
		break;
	  case wkbPoint:
		break;
	  case wkbLineString:
		{
		OGRLineString* line = NULL;

		//while ((feature = layer->GetNextFeature()) != NULL)
		//{

		  current_feature = feature;
		  // here we should check feature with, for example, default filter
		  for(int i = 0; i < styles_num; ++i)
		  {
			jobject style = env->GetObjectArrayElement(styles_array, i);
			jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);
			if(env->CallBooleanMethod(filter, wh.filter.check))
			{
			  paint_pen = env->GetObjectField(style, wh.vstyle.paint_pen);
			  env->DeleteLocalRef(style);
			  env->DeleteLocalRef(filter);
			  break;
			}
			// If nothing is applicable default paint remains

			env->DeleteLocalRef(style);
			env->DeleteLocalRef(filter);
		  }

		  line = (OGRLineString*) feature->GetGeometryRef();
		  float x0, y0, x1, y1;
		  for (int i = 1; i < line->getNumPoints(); ++i)
		  {
			x0 = (line->getX(i - 1) - left) * koeffX;
			x1 = (line->getX(i) - left) * koeffX;

			y0 = bitmapHeight - (line->getY(i - 1) - bottom) * koeffY;
			y1 = bitmapHeight - (line->getY(i) - bottom) * koeffY;

			env->CallVoidMethod(canvas, wh.canvas.drawLine,
								x0,
								y0,
								x1,
								y1,
								paint_pen);

			/*if(!env->IsSameObject(paint, default_paint))
			{
			  env->DeleteLocalRef(paint);
			  paint = default_paint;
			}*/
		  }
		//} // while

		break;
	  }
	  case wkbPolygon:
		{

		OGRPolygon* polygon = NULL;
		//
		// Parse rings
		//while ((feature = layer->GetNextFeature()) != NULL)
		//{
			 jobject path = env->NewObject(wh.path.class_, wh.path.path);
		  // here we should check feature with, for example, default filter
		  for(int i = 0; i < styles_num; ++i)
		  {
			jobject style = env->GetObjectArrayElement(styles_array, i);
			jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);

			if(env->CallBooleanMethod(filter, wh.filter.check))
			{
			  paint_pen = env->GetObjectField(style, wh.vstyle.paint_pen);
			  paint_brush = env->GetObjectField(style, wh.vstyle.paint_brush);
			  env->DeleteLocalRef(style);
			  env->DeleteLocalRef(filter);
			  break;
			}
			// If nothing is applicable default paint remains

			env->DeleteLocalRef(style);
			env->DeleteLocalRef(filter);
		  }

		  OGRPoint point;
		  float x, y;
		  polygon = (OGRPolygon*) feature->GetGeometryRef();


		  //  === Exterior ring ===
		  OGRLinearRing* ring = polygon->getExteriorRing();

		  // first point
		  ring->getPoint(0, &point);
		  x = (point.getX() - left) * koeffX;
		  y = (bitmapHeight - (point.getY() - bottom) * koeffY);
		  env->CallVoidMethod(path, wh.path.moveTo, x, y);
		  // all other points
		  for (int i = 1; i < ring->getNumPoints(); ++i)
		  {
			ring->getPoint(i, &point);
			x = (point.getX() - left) * koeffX;
			y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			env->CallVoidMethod(path, wh.path.lineTo, x, y);
		  }
		  //env->CallVoidMethod(canvas, wh.canvas.drawPath, path, paint);
		  env->CallVoidMethod(path, wh.path.closePolygon);

		  // === Interior rings ===
		  for (int r = 0; r < polygon->getNumInteriorRings(); ++r)
		  {
			ring = polygon->getInteriorRing(r);

			// first point
			ring->getPoint(0, &point);
			x = (point.getX() - left) * koeffX;
			y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			env->CallVoidMethod(path, wh.path.moveTo, x, y);

			// other points
			for (int i = 1; i < ring->getNumPoints(); ++i)
			{
			  ring->getPoint(i, &point);
			  x = (point.getX() - left) * koeffX;
			  y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			  env->CallVoidMethod(path, wh.path.lineTo, x, y);

			}
			env->CallVoidMethod(path, wh.path.closePolygon);
		  }

		  // draw Path with current feature
		  env->CallVoidMethod(canvas, wh.canvas.drawPath, path, paint_brush);
		  env->CallVoidMethod(canvas, wh.canvas.drawPath, path, paint_pen);
		  /*if(!env->IsSameObject(paint, default_paint))
		  {
			env->DeleteLocalRef(paint);
			paint = default_paint;
		  }*/
		  env->CallVoidMethod(path, wh.path.rewind);
		  env->DeleteLocalRef(path);
	   // } // while

		break;

	  }
	  case wkbMultiPoint:
		{
		OGRMultiPoint* multi_point = NULL;
		OGRPoint* point = NULL;

		//while ((feature = layer->GetNextFeature()) != NULL)
		//{
		  current_feature = feature;

		  // here we should check feature with, for example, default filter
		  for(int i = 0; i < styles_num; ++i)
		  {
			jobject style = env->GetObjectArrayElement(styles_array, i);
			jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);


			if(env->CallBooleanMethod(filter, wh.filter.check))
			{
				paint_pen = env->GetObjectField(style, wh.vstyle.paint_pen);
			  if (NULL == paint_pen)
				image = env->GetObjectField(style, wh.vstyle.image_id);
			  env->DeleteLocalRef(style);
			  env->DeleteLocalRef(filter);
			  break;
			}

			env->DeleteLocalRef(style);
			env->DeleteLocalRef(filter);

			// If nothing is applicable default paint remains
		  }

		  float x, y;
		  multi_point = (OGRMultiPoint*) feature->GetGeometryRef();
		  for (int g = 0; g < multi_point->getNumGeometries(); ++g)
		  {
			point = (OGRPoint*) multi_point->getGeometryRef(g);
			x = (point->getX() - left) * koeffX;
			y = (bitmapHeight - (point->getY() - bottom) * koeffY);

			// TODO: Hard-coded metro sign position!!! Done
			if (NULL != paint_pen)
			  env->CallVoidMethod(canvas, wh.canvas.drawCircle, x, y, 8.0*scale, paint_pen);
			else
			{
				jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
				//TODO: fatal signal 11 (sigsegv) at 0xdeadbaad
				if(bitmapClass == NULL)return -1;
				jmethodID getheight = env->GetMethodID(bitmapClass, "getHeight", "()I");
				if(getheight == NULL)return -1;
				jmethodID getwidth = env->GetMethodID(bitmapClass, "getWidth", "()I");
				if(getwidth == NULL)return -1;
				int height = env->CallIntMethod(image, getheight);
				int width = env->CallIntMethod(image, getwidth);
				jclass rectClass = env->FindClass("android/graphics/Rect");
				if(rectClass == NULL)return -1;
				jmethodID rectInit = env->GetMethodID(rectClass,"<init>","(IIII)V");
				if(rectInit == NULL)return -1;
				jobject src = env->NewObject(rectClass, rectInit, 0, 0, width, height);
				height = height*scale;
				width = width*scale;
				jobject dst = env->NewObject(rectClass, rectInit, int(x-width/2), int(y-height/2), int(width/2 + x), int(height/2 + y));
				jmethodID drawBitmap_method = env->GetMethodID(wh.canvas.class_, "drawBitmap", "(Landroid/graphics/Bitmap;Landroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Paint;)V");
				if(drawBitmap_method == NULL)return -1;
				env->CallVoidMethod(canvas, drawBitmap_method, image, src, dst, NULL);
				env->DeleteLocalRef(rectClass);
				env->DeleteLocalRef(bitmapClass);
			}
		  }

		  /*if (NULL == paint)
			paint = default_paint;

		  if(!env->IsSameObject(paint, default_paint))
		  {
			env->DeleteLocalRef(paint);
			paint = default_paint;
		  }*/

		//}
		break;
	  }
	  case wkbMultiLineString:
	  {
		  OGRGeometryCollection* multy = (OGRGeometryCollection*)feature->GetGeometryRef();
		  for(int i = 0; i < multy->getNumGeometries(); i++ )
		  {
//
			  OGRGeometry * pol =	multy->getGeometryRef(i);
			  OGRLineString* line = NULL;
			  line = (OGRLineString*) pol;
				//while ((feature = layer->GetNextFeature()) != NULL)
				//{
				  current_feature = feature;
				  // here we should check feature with, for example, default filter
				  for(int i = 0; i < styles_num; ++i)
				  {
					jobject style = env->GetObjectArrayElement(styles_array, i);
					jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);
					if(env->CallBooleanMethod(filter, wh.filter.check))
					{
					  paint_pen = env->GetObjectField(style, wh.vstyle.paint_pen);
					  env->DeleteLocalRef(style);
					  env->DeleteLocalRef(filter);
					  break;
					}
					// If nothing is applicable default paint remains

					env->DeleteLocalRef(style);
					env->DeleteLocalRef(filter);
				  }

				  line = (OGRLineString*) feature->GetGeometryRef();
				  float x0, y0, x1, y1;
				  for (int i = 1; i < line->getNumPoints(); ++i)
				  {
					x0 = (line->getX(i - 1) - left) * koeffX;
					x1 = (line->getX(i) - left) * koeffX;

					y0 = bitmapHeight - (line->getY(i - 1) - bottom) * koeffY;
					y1 = bitmapHeight - (line->getY(i) - bottom) * koeffY;

					env->CallVoidMethod(canvas, wh.canvas.drawLine,
										x0,
										y0,
										x1,
										y1,
										paint_pen);

					/*if(!env->IsSameObject(paint, default_paint))
					{
					  env->DeleteLocalRef(paint);
					  paint = default_paint;
					}*/
				  }
//
		  }
		break;
	  }
	  case wkbMultiPolygon:
	  {
		  //__android_log_print(ANDROID_LOG_VERBOSE, "NATIVE",  "multypolygon");
		  OGRGeometryCollection* multy = (OGRGeometryCollection*)feature->GetGeometryRef();
		  for(int i = 0; i < multy->getNumGeometries(); i++ )
		  {
			  OGRGeometry * pol =	multy->getGeometryRef(i);
				OGRPolygon* polygon = NULL;
			  jobject path = env->NewObject(wh.path.class_, wh.path.path);
			  // here we should check feature with, for example, default filter
			  for(int i = 0; i < styles_num; ++i)
			  {
				jobject style = env->GetObjectArrayElement(styles_array, i);
				jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);

				if(env->CallBooleanMethod(filter, wh.filter.check))
				{
					paint_pen = env->GetObjectField(style, wh.vstyle.paint_pen);
				  paint_brush = env->GetObjectField(style, wh.vstyle.paint_brush);
				  env->DeleteLocalRef(style);
				  env->DeleteLocalRef(filter);
				  break;
				}
				// If nothing is applicable default paint remains

				env->DeleteLocalRef(style);
				env->DeleteLocalRef(filter);
			  }

			  OGRPoint point;
			  float x, y;
			  polygon = (OGRPolygon*) pol;


			  //  === Exterior ring ===
			  OGRLinearRing* ring = polygon->getExteriorRing();

			  // first point
			  ring->getPoint(0, &point);
			  x = (point.getX() - left) * koeffX;
			  y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			  env->CallVoidMethod(path, wh.path.moveTo, x, y);
			  // all other points
			  for (int i = 1; i < ring->getNumPoints(); ++i)
			  {
				ring->getPoint(i, &point);
				x = (point.getX() - left) * koeffX;
				y = (bitmapHeight - (point.getY() - bottom) * koeffY);
				env->CallVoidMethod(path, wh.path.lineTo, x, y);
			  }
			  //env->CallVoidMethod(canvas, wh.canvas.drawPath, path, paint);
			  env->CallVoidMethod(path, wh.path.closePolygon);

			  // === Interior rings ===
			  for (int r = 0; r < polygon->getNumInteriorRings(); ++r)
			  {
				ring = polygon->getInteriorRing(r);

				// first point
				ring->getPoint(0, &point);
				x = (point.getX() - left) * koeffX;
				y = (bitmapHeight - (point.getY() - bottom) * koeffY);
				env->CallVoidMethod(path, wh.path.moveTo, x, y);

				// other points
				for (int i = 1; i < ring->getNumPoints(); ++i)
				{
				  ring->getPoint(i, &point);
				  x = (point.getX() - left) * koeffX;
				  y = (bitmapHeight - (point.getY() - bottom) * koeffY);
				  env->CallVoidMethod(path, wh.path.lineTo, x, y);

				}
				env->CallVoidMethod(path, wh.path.closePolygon);
			  }

			  // draw Path with current feature
			  env->CallVoidMethod(canvas, wh.canvas.drawPath, path, paint_pen);
			  env->CallVoidMethod(canvas, wh.canvas.drawPath, path, paint_brush);
			 /*if(!env->IsSameObject(paint, default_paint))
			  {
				env->DeleteLocalRef(paint);
				paint = default_paint;
			  }*/
			  env->CallVoidMethod(path, wh.path.rewind);
			  env->DeleteLocalRef(path);
		  }
	  }
		break;
	  case wkbGeometryCollection:
		break;
	  case wkbNone:
		break;
	  case wkbLinearRing:
		break;
	  case wkbPoint25D:
		break;
	  case wkbLineString25D:
		break;
	  case wkbPolygon25D:
		break;
	  case wkbMultiPoint25D:
		break;
	  case wkbMultiLineString25D:
		break;
	  case wkbMultiPolygon25D:
		break;
	  case wkbGeometryCollection25D:
		break;
	  }
  }
  env->DeleteLocalRef(paint_pen);
  env->DeleteLocalRef(paint_brush);
  return 0;
}

JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIEditableRenderer_drawFeature(JNIEnv* env, jobject thiz,
		jstring wkt, jint bitmapWidth, jint bitmapHeight, jobject area, jfloat scale)
{
	  jobject canvas =      env->GetObjectField(thiz, wh.renderer.canvas_id);
	  jobject vstyle =      env->GetObjectField(thiz, wh.renderer.style_id);
	  jobject add_styles =  env->GetObjectField(thiz, wh.renderer.add_styles_id);

	  int styles_num = env->CallIntMethod(add_styles, wh.array_list.size);
	  jobjectArray styles_array =
	    (jobjectArray) env->CallObjectMethod(add_styles, wh.array_list.toArray);

	  jobject paint_pen = env->GetObjectField(vstyle, wh.vstyle.paint_pen);
	  jobject paint_brush = env->GetObjectField(vstyle, wh.vstyle.paint_brush);
	  //jobject paint = default_paint;
	  jobject image = env->GetObjectField(vstyle, wh.vstyle.image_id);

	  // GIBounds
	  double left =   env->GetDoubleField(area, wh.bounds.left_id);
	  double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
	  double right =  env->GetDoubleField(area, wh.bounds.right_id);
	  double top =    env->GetDoubleField(area, wh.bounds.top_id);
	  // pixel koeff
	  float koeffX = (float) (bitmapWidth / (right - left));
	  float koeffY = (float) (bitmapHeight / (top - bottom));

      OGRGeometry *poGeometry;
      //char* pszWKT = const_cast<char*>(env->GetStringUTFChars(wkt, 0));
      char* pszWKT = (char*)(env->GetStringUTFChars(wkt, 0));


      OGRGeometryFactory::createFromWkt( &pszWKT, NULL, &poGeometry );
      //TODO env->ReleaseStringUTFChars(symantic, symantic_string);
      int res = 0;
	  OGRwkbGeometryType featureType = poGeometry->getGeometryType();
		switch (featureType)
		{
			case wkbUnknown:
			{
				res = 0;
			break;
			}
			case wkbPoint:
			{
				res = 1;
			break;
			}
			case wkbLineString:
			{
				OGRLineString* line = NULL;
				  //current_feature = feature;
				  // here we should check feature with, for example, default filter
				  for(int i = 0; i < styles_num; ++i)
				  {
					jobject style = env->GetObjectArrayElement(styles_array, i);
					jobject filter = env->GetObjectField(style, wh.vstyle.filter_id);
					if(env->CallBooleanMethod(filter, wh.filter.check))
					{
					  paint_pen = env->GetObjectField(style, wh.vstyle.paint_pen);
					  env->DeleteLocalRef(style);
					  env->DeleteLocalRef(filter);
					  break;
					}
					// If nothing is applicable default paint remains
					env->DeleteLocalRef(style);
					env->DeleteLocalRef(filter);
				  }

				  line = (OGRLineString*) poGeometry;
				  float x0, y0, x1, y1;

				  for (int i = 1; i < line->getNumPoints(); ++i)
				  {
					x0 = (line->getX(i - 1) - left) * koeffX;
					x1 = (line->getX(i) - left) * koeffX;

					y0 = bitmapHeight - (line->getY(i - 1) - bottom) * koeffY;
					y1 = bitmapHeight - (line->getY(i) - bottom) * koeffY;

					env->CallVoidMethod(canvas, wh.canvas.drawLine,	x0,	y0,	x1,	y1,	paint_pen);

				  }
				res = 2;
				break;
			}
			case wkbPolygon:
			{
				res = 3;
				break;
			}
			case wkbMultiPoint:
			{
				res = 4;
			break;
			}
			case wkbMultiLineString:
			{
				res = 5;
			break;
			}
			case wkbMultiPolygon:
			{
				res = 6;
			break;
			}
			case wkbGeometryCollection:
			{
				res = 7;
			break;
			}
			case wkbNone:
			{
				res = 8;
			break;
			}
			case wkbLinearRing:
			{
				res = 9;
			break;
			}
			case wkbPoint25D:
			{
			return 10;
			break;
			}
			case wkbLineString25D:
			{
				res = 11;
			break;
			}
			case wkbPolygon25D:
			{
				res = 12;
			break;
			}
			case wkbMultiPoint25D:
			{
				res = 13;
			break;
			}
			case wkbMultiLineString25D:
			{
				res = 14;
			break;
			}
			case wkbMultiPolygon25D:
			{
				res = 15;
			break;
			}
			case wkbGeometryCollection25D:
			{
				res = 16;
			break;
			}
		}
	  return res;
}


JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_drawText(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jint bitmapWidth,
    jint bitmapHeight,
    jobject area,
    jobject encoding)
{
  jobject canvas =      env->GetObjectField(thiz, wh.renderer.canvas_id);
  jobject vstyle =      env->GetObjectField(thiz, wh.renderer.style_id);
  jobject paint =       env->GetObjectField(vstyle, wh.vstyle.paint_pen);


  double left =   env->GetDoubleField(area, wh.bounds.left_id);
  double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
  double right =  env->GetDoubleField(area, wh.bounds.right_id);
  double top =    env->GetDoubleField(area, wh.bounds.top_id);

  OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
  OGRFeature* feature = NULL;

  // pixel koeff
  float koeffX = (float) (bitmapWidth / (right - left));
  float koeffY = (float) (bitmapHeight / (top - bottom));

  layer->ResetReading();
  layer->SetSpatialFilterRect(left, bottom, right, top);

  OGRwkbGeometryType layerType = layer->GetGeomType();

  switch (layerType)
  {
  case wkbUnknown:
    break;
  case wkbPoint:
    break;
  case wkbLineString:
    break;
  case wkbPolygon:
    break;
  case wkbMultiPoint:
    {
    OGRMultiPoint* multi_point = NULL;
    OGRPoint* point = NULL;

    while ((feature = layer->GetNextFeature()) != NULL)
    {
      float x, y;
      const char* text = feature->GetFieldAsString(5);

      jbyteArray encoded_text = env->NewByteArray(strlen(text));
      env->SetByteArrayRegion(encoded_text, 0, strlen(text), (jbyte*) text);
      jstring text_string = (jstring)env->CallObjectMethod(encoding, wh.encoding.decode, encoded_text);

      multi_point = (OGRMultiPoint*) feature->GetGeometryRef();
      for (int g = 0; g < multi_point->getNumGeometries(); ++g)
      {
        point = (OGRPoint*) multi_point->getGeometryRef(g);
        x = (point->getX() - left) * koeffX;
        y = (bitmapHeight - (point->getY() - bottom) * koeffY);

        env->CallVoidMethod(canvas, wh.canvas.drawText, text_string, x, y, paint);
      }
      env->DeleteLocalRef(text_string);
      env->DeleteLocalRef(encoded_text);
    }
    break;
  }
  case wkbMultiLineString:
    break;
  case wkbMultiPolygon:
    break;
  case wkbGeometryCollection:
    break;
  case wkbNone:
    break;
  case wkbLinearRing:
    break;
  case wkbPoint25D:
    break;
  case wkbLineString25D:
    break;
  case wkbPolygon25D:
    break;
  case wkbMultiPoint25D:
    break;
  case wkbMultiLineString25D:
    break;
  case wkbMultiPolygon25D:
    break;
  case wkbGeometryCollection25D:
    break;
  }

  return 0;
}


JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_getText(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jint bitmapWidth,
    jint bitmapHeight,
    jobject area,
    jobject encoding,
    jobject labels)
{
  double left =   env->GetDoubleField(area, wh.bounds.left_id);
  double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
  double right =  env->GetDoubleField(area, wh.bounds.right_id);
  double top =    env->GetDoubleField(area, wh.bounds.top_id);

  OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
  OGRFeature* feature = NULL;

  // pixel koeff
  float koeffX = (float) (bitmapWidth / (right - left));
  float koeffY = (float) (bitmapHeight / (top - bottom));

  layer->ResetReading();
  layer->SetSpatialFilterRect(left, bottom, right, top);

  OGRwkbGeometryType layerType = layer->GetGeomType();

  switch (layerType)
  {
  case wkbUnknown:
    break;
  case wkbPoint:
    break;
  case wkbLineString:
    break;
  case wkbPolygon:
    break;
  case wkbMultiPoint:
    {
    OGRMultiPoint* multi_point = NULL;
    OGRPoint* point = NULL;


    while ((feature = layer->GetNextFeature()) != NULL)
    {
      float x, y;
      const char* text = feature->GetFieldAsString(5);

      jbyteArray encoded_text = env->NewByteArray(strlen(text));
      env->SetByteArrayRegion(encoded_text, 0, strlen(text), (jbyte*) text);
      jstring text_string = (jstring)env->CallObjectMethod(encoding, wh.encoding.decode, encoded_text);

      multi_point = (OGRMultiPoint*) feature->GetGeometryRef();
      for (int g = 0; g < multi_point->getNumGeometries(); ++g)
      {
        point = (OGRPoint*) multi_point->getGeometryRef(g);
        x = (point->getX() - left) * koeffX;
        y = (bitmapHeight - (point->getY() - bottom) * koeffY);

        jobject pointf = env->NewObject(wh.pointf.class_, wh.pointf.pointf, x, y);
        env->CallVoidMethod(labels, wh.map.put, pointf, text_string);

        env->DeleteLocalRef(pointf);
      }
      env->DeleteLocalRef(text_string);
      env->DeleteLocalRef(encoded_text);
    }
    break;
  }
  case wkbMultiLineString:
    break;
  case wkbMultiPolygon:
    break;
  case wkbGeometryCollection:
    break;
  case wkbNone:
    break;
  case wkbLinearRing:
    break;
  case wkbPoint25D:
    break;
  case wkbLineString25D:
    break;
  case wkbPolygon25D:
    break;
  case wkbMultiPoint25D:
    break;
  case wkbMultiLineString25D:
    break;
  case wkbMultiPolygon25D:
    break;
  case wkbGeometryCollection25D:
    break;
  }

  return 0;
}
/*
JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_getStr(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jstring symantic,
    jlong bitmapWidth,
    jlong bitmapHeight,
    jobject area,
    jobject encoding,
    jobject labeled_layer)
{
	double left =   env->GetDoubleField(area, wh.bounds.left_id);
	double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
	double right =  env->GetDoubleField(area, wh.bounds.right_id);
	double top =    env->GetDoubleField(area, wh.bounds.top_id);

	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);

	float koeffX = (float) (bitmapWidth / (right - left));
	float koeffY = (float) (bitmapHeight / (top - bottom));

	layer->SetSpatialFilterRect(left, bottom, right, top);
	char query[128];
	const char* symantic_string = env->GetStringUTFChars(symantic, 0);
	//for geometry debugging
	//sprintf(query, "(OBJECTID = %s)", symantic_string);
	sprintf(query, "(%s IS NOT NULL)", symantic_string);
	layer->SetAttributeFilter(query);
	layer->ResetReading();
	int res = layer->GetFeatureCount();

	OGRwkbGeometryType layerType = layer->GetGeomType();

	switch (layerType)
	{
		case wkbUnknown:
		{
		return 0;
		break;
		}
		case wkbPoint:
		{
		return 1;
		break;
		}
		case wkbLineString:
		{
		    OGRLineString* ogr_line = NULL;
			OGRPoint* point = NULL;
			OGRFeature* feature = NULL;
		    while ((feature = layer->GetNextFeature()) != NULL)
		    {
		    	//TODO: for geometry debugging
		    	const char* text = feature->GetFieldAsString(feature->GetFieldIndex(symantic_string));
		    	//const char* text = feature->GetFieldAsString(3);
				ogr_line = (OGRLineString*) feature->GetGeometryRef();
				jstring str = env->NewStringUTF(text);
				if(strlen(text) > 0)
				{

				jobject line = env->NewObject(wh.line.class_, wh.line.line, str);
				OGRPoint point;
				  float x, y;

				  for (int i = 0; i < ogr_line->getNumPoints(); ++i)
				  {
					ogr_line->getPoint(i, &point);
					x = (point.getX() - left) * koeffX;
					y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			        jobject pointf = env->NewObject(wh.pointf.class_, wh.pointf.pointf, x, y);
			        env->CallVoidMethod(line, wh.line.add_pointf, pointf);

			        env->DeleteLocalRef(pointf);
				  }

				  env->CallVoidMethod(labeled_layer, wh.labeled_layer.add_label, line);
				  env->DeleteLocalRef(line);
				}
				env->DeleteLocalRef(str);
		    }
			break;
		}
		case wkbPolygon:
		{
			//
			OGRPolygon* ogr_polygon = NULL;
			OGRPoint* point = NULL;
			OGRFeature* feature = NULL;
		    while ((feature = layer->GetNextFeature()) != NULL)
		    {
				const char* text = feature->GetFieldAsString(feature->GetFieldIndex(symantic_string));
				ogr_polygon = (OGRPolygon*) feature->GetGeometryRef();
				jstring str = env->NewStringUTF(text);
				if(strlen(text) > 0)
				{

				jobject polygon = env->NewObject(wh.polygon.class_, wh.polygon.polygon, str);
				OGRPoint point;
				  float x, y;
				  //  === Exterior ring ===
				  OGRLinearRing* ring = ogr_polygon->getExteriorRing();
				  for (int i = 0; i < ring->getNumPoints(); ++i)
				  {
					ring->getPoint(i, &point);
					x = (point.getX() - left) * koeffX;
					y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			        jobject pointf = env->NewObject(wh.pointf.class_, wh.pointf.pointf, x, y);
			        env->CallVoidMethod(polygon, wh.polygon.add_pointf, pointf);

			        env->DeleteLocalRef(pointf);
				  }
			      // === Interior rings ===
			      for (int r = 0; r < ogr_polygon->getNumInteriorRings(); ++r)
			      {
					jobject ring_geometry = env->NewObject(wh.polygon.class_, wh.polygon.polygon, str);
					ring = ogr_polygon->getInteriorRing(r);
			        for (int i = 0; i < ring->getNumPoints(); ++i)
			        {
			          ring->getPoint(i, &point);
			          x = (point.getX() - left) * koeffX;
			          y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			          jobject pointf = env->NewObject(wh.pointf.class_, wh.pointf.pointf, x, y);
			          env->CallVoidMethod(ring_geometry, wh.polygon.add_pointf, pointf);
			          env->DeleteLocalRef(pointf);
			        }
			        env->CallVoidMethod(polygon, wh.polygon.add_interior_ring, ring_geometry);
			        env->DeleteLocalRef(ring_geometry);
			      }

				  env->CallVoidMethod(labeled_layer, wh.labeled_layer.add_label, polygon);
				  env->DeleteLocalRef(polygon);
				}
				env->DeleteLocalRef(str);
		    }
			break;
		}
		case wkbMultiPoint:
		{
		return 4;
		break;
		}
		case wkbMultiLineString:
		{
		return 5;
		break;
		}
		case wkbMultiPolygon:
		{
		return 6;
		break;
		}
		case wkbGeometryCollection:
		{
		return 7;
		break;
		}
		case wkbNone:
		{
		return 8;
		break;
		}
		case wkbLinearRing:
		{
		return 9;
		break;
		}
		case wkbPoint25D:
		{
		return 10;
		break;
		}
		case wkbLineString25D:
		{
		return 11;
		break;
		}
		case wkbPolygon25D:
		{
		return 12;
		break;
		}
		case wkbMultiPoint25D:
		{
		return 13;
		break;
		}
		case wkbMultiLineString25D:
		{
		return 14;
		break;
		}
		case wkbMultiPolygon25D:
		{
		return 15;
		break;
		}
		case wkbGeometryCollection25D:
		{
		return 16;
		break;
		}
	}
	layer->SetAttributeFilter(NULL);
	layer->ResetReading();
	env->ReleaseStringUTFChars(symantic, symantic_string);
	return res;
}
*/
JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_getLabel(JNIEnv* env,
    jobject thiz,
    jlong layer_id,
    jstring symantic,
    jstring conditions,
    jlong bitmapWidth,
    jlong bitmapHeight,
    jobject area,
    jobject encoding,
    jobject labeled_layer)
{
	double left =   env->GetDoubleField(area, wh.bounds.left_id);
	double bottom = env->GetDoubleField(area, wh.bounds.bottom_id);
	double right =  env->GetDoubleField(area, wh.bounds.right_id);
	double top =    env->GetDoubleField(area, wh.bounds.top_id);

	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);

	float koeffX = (float) (bitmapWidth / (right - left));
	float koeffY = (float) (bitmapHeight / (top - bottom));

	layer->SetSpatialFilterRect(left, bottom, right, top);
	char query[128];
	const char* symantic_string = env->GetStringUTFChars(symantic, 0);
	const char* condition_string = env->GetStringUTFChars(conditions, 0);

	sprintf(query, "(%s)", condition_string);

	layer->SetAttributeFilter(query);
	layer->ResetReading();
	int res = layer->GetFeatureCount();

	OGRwkbGeometryType layerType = layer->GetGeomType();

	switch (layerType)
	{
		case wkbUnknown:
		{
			return 0;
		}
		case wkbPoint:
		{
			return 1;
		}
		case wkbLineString:
		{
		    OGRLineString* ogr_line = NULL;
			OGRPoint* point = NULL;
			OGRFeature* feature = NULL;
		    while ((feature = layer->GetNextFeature()) != NULL)
		    {
		    	//TODO: for geometry debugging
		    	const char* text = feature->GetFieldAsString(feature->GetFieldIndex(symantic_string));
				ogr_line = (OGRLineString*) feature->GetGeometryRef();
				jstring str = env->NewStringUTF(text);
				if(strlen(text) > 0)
				{

				jobject line = env->NewObject(wh.line.class_, wh.line.line, str);
				OGRPoint point;
				  float x, y;

				  for (int i = 0; i < ogr_line->getNumPoints(); ++i)
				  {
					ogr_line->getPoint(i, &point);
					x = (point.getX() - left) * koeffX;
					y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			        jobject pointf = env->NewObject(wh.pointf.class_, wh.pointf.pointf, x, y);
			        env->CallVoidMethod(line, wh.line.add_pointf, pointf);

			        env->DeleteLocalRef(pointf);
				  }

				  env->CallVoidMethod(labeled_layer, wh.labeled_layer.add_label, line);
				  env->DeleteLocalRef(line);
				}
				env->DeleteLocalRef(str);
		    }
			break;
		}
		case wkbPolygon:
		{
			//
			OGRPolygon* ogr_polygon = NULL;
			OGRPoint* point = NULL;
			OGRFeature* feature = NULL;
		    while ((feature = layer->GetNextFeature()) != NULL)
		    {
				const char* text = feature->GetFieldAsString(feature->GetFieldIndex(symantic_string));
				ogr_polygon = (OGRPolygon*) feature->GetGeometryRef();
				jstring str = env->NewStringUTF(text);
				if(strlen(text) > 0)
				{

				jobject polygon = env->NewObject(wh.polygon.class_, wh.polygon.polygon, str);
				OGRPoint point;
				  float x, y;
				  //  === Exterior ring ===
				  OGRLinearRing* ring = ogr_polygon->getExteriorRing();
				  for (int i = 0; i < ring->getNumPoints(); ++i)
				  {
					ring->getPoint(i, &point);
					x = (point.getX() - left) * koeffX;
					y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			        jobject pointf = env->NewObject(wh.pointf.class_, wh.pointf.pointf, x, y);
			        env->CallVoidMethod(polygon, wh.polygon.add_pointf, pointf);

			        env->DeleteLocalRef(pointf);
				  }
			      // === Interior rings ===
			      for (int r = 0; r < ogr_polygon->getNumInteriorRings(); ++r)
			      {
					jobject ring_geometry = env->NewObject(wh.polygon.class_, wh.polygon.polygon, str);
					ring = ogr_polygon->getInteriorRing(r);
			        for (int i = 0; i < ring->getNumPoints(); ++i)
			        {
			          ring->getPoint(i, &point);
			          x = (point.getX() - left) * koeffX;
			          y = (bitmapHeight - (point.getY() - bottom) * koeffY);
			          jobject pointf = env->NewObject(wh.pointf.class_, wh.pointf.pointf, x, y);
			          env->CallVoidMethod(ring_geometry, wh.polygon.add_pointf, pointf);
			          env->DeleteLocalRef(pointf);
			        }
			        env->CallVoidMethod(polygon, wh.polygon.add_interior_ring, ring_geometry);
			        env->DeleteLocalRef(ring_geometry);
			      }

				  env->CallVoidMethod(labeled_layer, wh.labeled_layer.add_label, polygon);
				  env->DeleteLocalRef(polygon);
				}
				env->DeleteLocalRef(str);
		    }
			break;
		}
		case wkbMultiPoint:
		{
			return 4;
		}
		case wkbMultiLineString:
		{
			return 5;
		}
		case wkbMultiPolygon:
		{
			return 6;
		}
		case wkbGeometryCollection:
		{
			return 7;
		}
		case wkbNone:
		{
			return 8;
		}
		case wkbLinearRing:
		{
			return 9;
		}
		case wkbPoint25D:
		{
			return 10;
		}
		case wkbLineString25D:
		{
			return 11;
		}
		case wkbPolygon25D:
		{
			return 12;
		}
		case wkbMultiPoint25D:
		{
			return 13;
		}
		case wkbMultiLineString25D:
		{
			return 14;
		}
		case wkbMultiPolygon25D:
		{
			return 15;
		}
		case wkbGeometryCollection25D:
		{

			return 16;
		}
	}
	layer->SetAttributeFilter(NULL);
	layer->ResetReading();
	env->ReleaseStringUTFChars(symantic, symantic_string);
	return res;
}
/**/

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_getLayerType(JNIEnv* env,
    jobject thiz,
    jlong layer_id)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	OGRwkbGeometryType layerType = layer->GetGeomType();
	switch (layerType)
	{
		case wkbUnknown:
		{
		return 0;
		}
		case wkbPoint:
		{
		return 1;
		}
		case wkbLineString:
		{
			return 2;
		}
		case wkbPolygon:
		{
			return 3;
		}
		case wkbMultiPoint:
		{
		return 4;
		}
		case wkbMultiLineString:
		{
		return 5;
		}
		case wkbMultiPolygon:
		{
		return 6;
		}
		case wkbGeometryCollection:
		{
		return 7;
		}
		case wkbNone:
		{
		return 8;
		}
		case wkbLinearRing:
		{
		return 9;
		}
		case wkbPoint25D:
		{
		return 10;
		}
		case wkbLineString25D:
		{
		return 11;
		}
		case wkbPolygon25D:
		{
		return 12;
		}
		case wkbMultiPoint25D:
		{
		return 13;
		}
		case wkbMultiLineString25D:
		{
		return 14;
		}
		case wkbMultiPolygon25D:
		{
		return 15;
		}
		case wkbGeometryCollection25D:
		{
		return 16;
		}
	}
	return -1;
}

JNIEXPORT jint
JNICALL
Java_ru_tcgeo_gilib_GIVectorRenderer_GetFieldIndex(JNIEnv* env, jobject thiz, jlong layer_id, jstring name)
{
	OGRLayer *layer = reinterpret_cast<OGRLayer *>(layer_id);
	const char* name_string = env->GetStringUTFChars(name, 0);
	OGRFeatureDefn* layerDF = layer->GetLayerDefn();
	return layerDF->GetFieldIndex(name_string);
}
