#include <jni.h>

#include <gdalwarper.h>
#include <gdal.h>
#include <gdal_priv.h>
#include <math.h>
#include <ogr_spatialref.h>
#include <android/bitmap.h>

#include <android/log.h>

/*
 * ***** IMPORTANT *****
 * 
 * This module has some problems:
 *  - It works fine only for rasters with exactly three channels RGB
 *  - It locks bitmap's pixels, so map reading can't be interrupted. 
 *  When implementing drawing-thread and ui-thread, drawing-thread should be stoppable.
 *  Maybe double bufferization can be used to save visible bitmap, and mutex with
 *  every n-points check will work fine to perform reading stop.
 *
 */

struct GDALInfo
{
  GDALDataset *GdalBaseDataset;
  int band_count;
  GDALRasterBand *raster_band[3];

  // See geoTransform description at GDAL docs
  double geoTransform[6];

  int rasterSizeX;
  int rasterSizeY;
  double minGeoX;
  double minGeoY;
  double maxGeoX;
  double maxGeoY;

};


extern "C"
{
	JNIEXPORT jdoubleArray
	JNICALL	Java_ru_tcgeo_gilib_GIRasterRenderer_reprojectPoint(JNIEnv* env, jobject thiz, jlong layer_id, jdouble X, jdouble Y, jlong source_id, jlong dest_id);

	JNIEXPORT jlong
	JNICALL	Java_ru_tcgeo_gilib_GIRasterLayer_getProjection(JNIEnv* env, jobject thiz, jlong layer_id);

  JNIEXPORT jint
  JNICALL Java_ru_tcgeo_gilib_GIRasterLayer_initRasterLayer (JNIEnv* env, 
    jobject thiz, jstring path);

  JNIEXPORT jint
  JNICALL Java_ru_tcgeo_gilib_GIRasterRenderer_readMap (JNIEnv* env,
                                                        jobject thiz,
                                                        jlong layer_id,
                                                        jobject bitmap,
                                                        jdouble fromX,
                                                        jdouble fromY,
                                                        jdouble toX,
                                                        jdouble toY);

}

union ColorValue {
  struct{
    char b;
    char g;
    char r;
    char a;
  } channel;
  int value;
};

JNIEXPORT jlong
JNICALL
Java_ru_tcgeo_gilib_GIRasterLayer_getProjection(JNIEnv* env, jobject thiz, jlong layer_id)
{
	GDALInfo* data = reinterpret_cast<GDALInfo *>(layer_id);
	const char* description = data->GdalBaseDataset->GetProjectionRef();
	OGRSpatialReference* dst = new OGRSpatialReference(description);
	return reinterpret_cast<long>(dst);
}

JNIEXPORT jdoubleArray
JNICALL	Java_ru_tcgeo_gilib_GIRasterRenderer_reprojectPoint(JNIEnv* env, jobject thiz, jlong layer_id, jdouble X, jdouble Y, jlong source_id, jlong dest_id)
{
	GDALInfo* data = reinterpret_cast<GDALInfo *>(layer_id);
	OGRSpatialReference* dst = NULL;
	const char* description = NULL;
	if(dest_id == 0)
	{
		description = data->GdalBaseDataset->GetProjectionRef();
		dst = new OGRSpatialReference(description);
	}
	else
	{
	    dst =   reinterpret_cast<OGRSpatialReference*>(dest_id);
	}
    OGRSpatialReference* source = reinterpret_cast<OGRSpatialReference*>(source_id);
	char* pszWKT_source = NULL;
	char* pszWKT_dest = NULL;
	source->exportToWkt(&pszWKT_source);
	dst->exportToWkt(&pszWKT_dest);
//	__android_log_print(ANDROID_LOG_VERBOSE, "NATIVE",  "source\n  %s\n  destination\n  %s", pszWKT_source, pszWKT_dest);
	OGRCoordinateTransformation* transformation;
	transformation = OGRCreateCoordinateTransformation(source, dst);
	double buf[2];
	buf[0] = X;
	buf[1] = Y;
	transformation->Transform(1, &buf[0], &buf[1]);
	OGRCoordinateTransformation::DestroyCT(transformation);
	jdoubleArray result = env->NewDoubleArray(2);
	env->SetDoubleArrayRegion(result, 0, 2, buf);
	return result;
}

JNIEXPORT jint
JNICALL Java_ru_tcgeo_gilib_GIRasterLayer_initRasterLayer (JNIEnv* env,
                                                    jobject thiz, jstring path)
{
    GDALInfo* data = new GDALInfo;
    data->GdalBaseDataset = NULL;
    
    const char* path_ = env->GetStringUTFChars(path, 0);
    data->GdalBaseDataset = static_cast<GDALDataset*>(GDALOpen(path_,
                                                              GA_ReadOnly));
    env->ReleaseStringUTFChars(path, path_);

    if ( data->GdalBaseDataset == 0 )
    {
      return -1;
    }

    // TODO: Works fine only for rasters with 3 bands
    data->band_count = data->GdalBaseDataset->GetRasterCount();
    for (int i = 0; i < data->band_count; ++i)
      data->raster_band[i] = data->GdalBaseDataset->GetRasterBand(i + 1);

    data->rasterSizeX = data->raster_band[0]->GetXSize();
    data->rasterSizeY = data->raster_band[0]->GetYSize();

    // Following conversions are based on GDAL docs
    data->GdalBaseDataset->GetGeoTransform(data->geoTransform);

    data->maxGeoX = (double)data->rasterSizeX * data->geoTransform[1] + data->geoTransform[0];
    data->maxGeoY = data->geoTransform[3];

    data->minGeoX = data->geoTransform[0];
    data->minGeoY = data->geoTransform[3] + data->rasterSizeY * data->geoTransform[5];

    return reinterpret_cast<long>(data);
}


JNIEXPORT jint
JNICALL Java_ru_tcgeo_gilib_GIRasterRenderer_readMap (JNIEnv* env,
                                                      jobject thiz,
                                                      jlong layer_id,
                                                      jobject bitmap,
                                                      jdouble fromX,
                                                      jdouble fromY,
                                                      jdouble toX,
                                                      jdouble toY)
{




  int *pixels = 0;
  void** pPixels = (void**)&pixels;
  char* bands[3];
  AndroidBitmap_lockPixels(env, bitmap, pPixels);
  if(pixels == 0)
    return 0x01;  // No pixels were locked

  AndroidBitmapInfo bitmap_info;
  AndroidBitmap_getInfo(env, bitmap, &bitmap_info);

  GDALInfo* data = reinterpret_cast<GDALInfo *>(layer_id);

  /*
  //arab

	const char* description = data->GdalBaseDataset->GetProjectionRef();
	OGRSpatialReference* dst = new OGRSpatialReference(description);
	OGRSpatialReference* source = new OGRSpatialReference();
	//srs->importFromEPSG(4326);
	//srs->SetWellKnownGeogCS("EPSG:4326");
	source->SetWellKnownGeogCS("WGS84");
	char* pszWKT = NULL;
	source->exportToWkt(&pszWKT);


	__android_log_print(ANDROID_LOG_VERBOSE, "NATIVE",  "source\n  %s\n  destination\n  %s", pszWKT, description);
	OGRCoordinateTransformation* transformation;
	transformation = OGRCreateCoordinateTransformation(source, dst);
	double buf_from[2];
	buf_from[0] = fromX;
	buf_from[1] = fromY;
	double buf_to[2];
	buf_to[0] = toX;
	buf_to[1] = toY;

    transformation->Transform(1, &buf[0], &buf[1]);
    OGRCoordinateTransformation::DestroyCT(transformation);

  */
  // we don't see our raster
  if(toX < data->minGeoX or
     fromY < data->minGeoY or // Y is inverted in our request, but is normal in raster
     fromX > data->maxGeoX or
     toY > data->maxGeoY)
    return -1;

  /*

eRWFlag   Either GF_Read to read a region of data, or GF_Write to write a region of data->
nXOff   The pixel offset to the top left corner of the region of the band to be accessed. This would be zero to start from the left side.
nYOff   The line offset to the top left corner of the region of the band to be accessed. This would be zero to start from the top.
nXSize  The width of the region of the band to be accessed in pixels.
nYSize  The height of the region of the band to be accessed in lines.
pData   The buffer into which the data should be read, or from which it should be written. This buffer must contain at least nBufXSize * nBufYSize * nBandCount words of type eBufType. It is organized in left to right,top to bottom pixel order. Spacing is controlled by the nPixelSpace, and nLineSpace parameters.
nBufXSize   the width of the buffer image into which the desired region is to be read, or from which it is to be written.
nBufYSize   the height of the buffer image into which the desired region is to be read, or from which it is to be written.
eBufType  the type of the pixel values in the pData data buffer. The pixel values will automatically be translated to/from the GDALRasterBand data type as needed.
nBandCount  the number of bands being read or written.
panBandMap  the list of nBandCount band numbers being read/written. Note band numbers are 1 based. This may be NULL to select the first nBandCount bands.
nPixelSpace   The byte offset from the start of one pixel value in pData to the start of the next pixel value within a scanline. If defaulted (0) the size of the datatype eBufType is used.
nLineSpace  The byte offset from the start of one scanline in pData to the start of the next. If defaulted (0) the size of the datatype eBufType * nBufXSize is used.
nBandSpace  the byte offset from the start of one bands data to the start of the next. If defaulted (0) the value will be nLineSpace * nBufYSize implying band sequential organization of the data buffer.
  */




  GDALRasterBand* best = 0;

  float sampleX = (data->maxGeoX - data->minGeoX) * bitmap_info.width;
  sampleX /= (toX - fromX);

  float sampleY = (data->maxGeoY - data->minGeoY) * bitmap_info.height;
  sampleY /= (fromY - toY);
  // ARAB
  best = data->raster_band[0]->GetRasterSampleOverview((int)(sampleX*sampleY));

  double pixelSizeX = (data->maxGeoX - data->minGeoX) / best->GetXSize();
  double pixelSizeY = (data->maxGeoY - data->minGeoY) / best->GetYSize();
  //ARAB
  /*int pixelLeftTopX = (fromX - data->minGeoX) / pixelSizeX;
  int pixelLeftTopY = (data->maxGeoY - fromY) / pixelSizeY;
  int pixelRightBottomX = (toX - data->minGeoX) / pixelSizeX;
  int pixelRightBottomY = (data->maxGeoY - toY) / pixelSizeY;*/

  int pixelLeftTopX = floor((fromX - data->minGeoX) / pixelSizeX);
  int pixelLeftTopY = floor((data->maxGeoY - fromY) / pixelSizeY);
  int pixelRightBottomX = ceil((toX - data->minGeoX) / pixelSizeX);
  int pixelRightBottomY = ceil((data->maxGeoY - toY) / pixelSizeY);
/*
  sprintf(buf, "raster_size xy: %dx%d", best->GetXSize(), best->GetYSize());
  __android_log_print(ANDROID_LOG_VERBOSE, "NATIVE", buf);
*/
  int raster_offset_x = (pixelLeftTopX > 0) ? 0 : abs(pixelLeftTopX);
  int raster_offset_y = (pixelLeftTopY > 0) ? 0 : abs(pixelLeftTopY);
  int raster_over_x = (pixelRightBottomX < best->GetXSize()) ?
                                    0 : pixelRightBottomX - best->GetXSize();
  int raster_over_y = (pixelRightBottomY < best->GetYSize()) ?
                                    0: pixelRightBottomY - best->GetYSize();

  double bm_pixel_width = double(pixelRightBottomX - pixelLeftTopX) / bitmap_info.width;
  double bm_pixel_height = double(pixelRightBottomY - pixelLeftTopY) / bitmap_info.height;

  pixelLeftTopX += raster_offset_x;
  pixelLeftTopY += raster_offset_y;
  pixelRightBottomX -= raster_over_x;
  pixelRightBottomY -= raster_over_y;

  int raster_size_x = pixelRightBottomX - pixelLeftTopX;
  int raster_size_y = pixelRightBottomY - pixelLeftTopY;
  //ARAB
  /*int bm_raster_offset_x = double(raster_offset_x) / bm_pixel_width;
  int bm_raster_offset_y = double(raster_offset_y) / bm_pixel_height;
  int bm_raster_size_x = double(raster_size_x) / bm_pixel_width;
  int bm_raster_size_y = double(raster_size_y) / bm_pixel_height;*/

  int bm_raster_offset_x = floor(double(raster_offset_x) / bm_pixel_width);
  int bm_raster_offset_y = floor(double(raster_offset_y) / bm_pixel_height);
  int bm_raster_size_x = ceil(double(raster_size_x) / bm_pixel_width);
  int bm_raster_size_y = ceil(double(raster_size_y) / bm_pixel_height);


/*
  sprintf(buf, "raster_new x-x: %dx%d", pixelLeftTopX, pixelRightBottomX);
  __android_log_print(ANDROID_LOG_VERBOSE, "NATIVE", buf);
  sprintf(buf, "raster_new y-y: %dx%d", pixelLeftTopY, pixelRightBottomY);
  __android_log_print(ANDROID_LOG_VERBOSE, "NATIVE", buf);

*/
  int amount = bm_raster_size_x * bm_raster_size_y;

  for(int i = 0; i < data->band_count; ++i)
  {

    best = data->raster_band[i]->GetRasterSampleOverview((int)(sampleX*sampleY));

    bands[i] = new char[amount];
    //TODO
    //__android_log_print(ANDROID_LOG_VERBOSE, "NATIVE",  "raster_size_x: %i \n raster_size_y %i ", raster_size_x, raster_size_y);
    if (CE_Failure == best->RasterIO(GF_Read, pixelLeftTopX, pixelLeftTopY, raster_size_x, raster_size_y,
                                     bands[i], bm_raster_size_x, bm_raster_size_y, GDT_Byte, 0, 0))
    {
      AndroidBitmap_unlockPixels(env, bitmap);
      return -1;
    }

  }

  ColorValue c;
  for (int y = 0; y < bm_raster_size_y; ++y)
  {
    for(int x = 0; x < bm_raster_size_x; ++x)
    {
      c.channel.b = bands[0][x + bm_raster_size_x*y];
      c.channel.g = bands[1][x + bm_raster_size_x*y];
      c.channel.r = bands[2][x + bm_raster_size_x*y];
      c.channel.a = 255;

      // pixels[x][y] = pixels[x + y*width]
      pixels[(x + bm_raster_offset_x) + ((y + bm_raster_offset_y)*bitmap_info.width)] = c.value;
    }
  }

  AndroidBitmap_unlockPixels(env, bitmap);
  return 0;

}
