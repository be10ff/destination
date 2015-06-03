package ru.tcgeo.application;

import java.io.File;
import java.util.ArrayList;

import ru.tcgeo.gilib.AddressSearchAdapterItem;
import ru.tcgeo.gilib.GIBounds;
import ru.tcgeo.gilib.GIColor;
import ru.tcgeo.gilib.GIControlFloating;
import ru.tcgeo.gilib.GIEditLayersKeeper;
import ru.tcgeo.gilib.GIEditableLayer;
import ru.tcgeo.gilib.GIEditableLayer.GIEditableLayerType;
import ru.tcgeo.gilib.GIEditableSQLiteLayer;
import ru.tcgeo.gilib.GIGroupLayer;
import ru.tcgeo.gilib.GILayer;
import ru.tcgeo.gilib.GILayer.GILayerType;
import ru.tcgeo.gilib.GILonLat;
import ru.tcgeo.gilib.GIMap;
import ru.tcgeo.gilib.GIPList;
import ru.tcgeo.gilib.GIProjection;
import ru.tcgeo.gilib.GIRuleToolControl;
import ru.tcgeo.gilib.GISQLLayer;
import ru.tcgeo.gilib.GISQLLayer.GISQLiteZoomingType;
import ru.tcgeo.gilib.GISQLRequest;
import ru.tcgeo.gilib.GIScaleRange;
import ru.tcgeo.gilib.GISquareToolControl;
import ru.tcgeo.gilib.GITouchControl;
import ru.tcgeo.gilib.GITuple;
import ru.tcgeo.gilib.GIVectorStyle;
import ru.tcgeo.gilib.gps.GIDirectionToPOIArrow;
import ru.tcgeo.gilib.gps.GIGPSButtonView;
import ru.tcgeo.gilib.gps.GIGPSLocationListener;
import ru.tcgeo.gilib.gps.GILocatorView;
import ru.tcgeo.gilib.parser.GIProjectProperties;
import ru.tcgeo.gilib.parser.GIPropertiesGroup;
import ru.tcgeo.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.gilib.parser.GIPropertiesLayerRef;
import ru.tcgeo.gilib.parser.GIRange;
import ru.tcgeo.gilib.parser.GISQLDB;
import ru.tcgeo.gilib.parser.GISource;
import ru.tcgeo.gilib.script.GIScriptExpression;
import ru.tcgeo.wkt.GI_WktGeometry;
import ru.tcgeo.wkt.GI_WktPoint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Geoinfo extends Activity implements IFolderItemListener// implements
																	// OnTouchListener
{
	public final String LOG_TAG = "myLogsMainApp";
	GIMap map;
	GITouchControl touchControl;
	SharedPreferences sp;

	final String SAVED_PATH = "default_project_path";
	Dialog projects_dialog;
	Dialog markers_dialog;
	Dialog editablelayers_dialog;
	GIScaleControl m_scale_control;
	ImageButton square_button;
	ImageButton rule_button;
	ImageButton follow_button;
	GIControlFloating m_marker_point;
	//LocationManager m_location_manager;
	GIGPSLocationListener m_location_listener;
	//View
	FrameLayout m_top_bar;
	//GICompassView m_compass_view;
	GILocatorView m_locator;
	GIGPSButtonView m_gps_button;

	public final static String edit_layer_tag = "EDIT_LAYER_TAG";
	public final IFolderItemListener m_fileOpenListener = this;

//	public class GPSLocationListener implements LocationListener 
//	{
//		public GILonLat m_location;
//		public void onLocationChanged(Location location) 
//		{
//			// Assuming we get wgs84 coordinates
//			m_location = new GILonLat(location.getLongitude(), location.getLatitude());
//
//			m_location = GIProjection.ReprojectLonLat(m_location, GIProjection.WGS84(), map.Projection());
//			// map.SetCenter(m_location);
//
//			// GILonLat deg = new GILonLat(location.getLongitude(),
//			// location.getLatitude());
//			// GIPositionControl.Instance(map.getContext(), map).setLonLat(deg);
//			GIEditLayersKeeper.Instance().onGPSLocationChanged(location);
//		}
//
//		public void onProviderDisabled(String provider) 
//		{
//			GIEditLayersKeeper.Instance().onGPSProviderEnabled(provider, false);
//		}
//
//		public void onProviderEnabled(String provider) 
//		{
//			GIEditLayersKeeper.Instance().onGPSProviderEnabled(provider, true);
//		}
//
//		public void onStatusChanged(String provider, int status, Bundle extras) 
//		{
//			GIEditLayersKeeper.Instance().onGPSStatusChanged(provider, status,	extras);
//		}
//	}

	// Class is a wrapper for GITuple to use in ArrayAdapter
	public class LayersAdapterItem {
		final public GITuple m_tuple;

		LayersAdapterItem(GITuple tuple) {
			m_tuple = tuple;
		}

		@Override
		public String toString() {
			return m_tuple.layer.getName();
		}
	}

	public class ProjectsAdapterItem {
		final public GIProjectProperties m_project_settings;

		ProjectsAdapterItem(GIProjectProperties proj) {
			m_project_settings = proj;
		}

		@Override
		public String toString() {
			return m_project_settings.m_name;
		}
	}

	public class MarkersAdapterItem {
		final public GIPList.GIMarker m_marker;

		MarkersAdapterItem(GIPList.GIMarker marker) {
			m_marker = marker;
		}

		@Override
		public String toString() {
			return m_marker.m_name + " " + m_marker.m_lon + ":"
					+ m_marker.m_lat;
		}

	}

	public class EditableLayersAdapterItem {
		final public GIEditableLayer m_layer;

		EditableLayersAdapterItem(GIEditableLayer layer) {
			m_layer = layer;
		}

		@Override
		public String toString() {
			return m_layer.getName();
		}

	}

	public class LayersAdapter extends ArrayAdapter<LayersAdapterItem> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final LayersAdapterItem item = getItem(position);
			View v = LayoutInflater.from(getContext()).inflate(
					R.layout.layers_list_item, null);
			((TextView) v.findViewById(R.id.layers_list_item_text))
					.setText(item.m_tuple.layer.getName());

			CheckBox checkbox = (CheckBox) v
					.findViewById(R.id.layers_list_item_switch);
			checkbox.setChecked(item.m_tuple.visible);

			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					item.m_tuple.visible = isChecked;
					map.UpdateMap();
				}
			});
			return v;
		}

		public LayersAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}
	}

	public class ProjectsAdapter extends ArrayAdapter<ProjectsAdapterItem> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ProjectsAdapterItem item = getItem(position);
			View v = LayoutInflater.from(getContext()).inflate(
					R.layout.project_selector_list_item, null);
			TextView text_name = (TextView) v
					.findViewById(R.id.project_list_item_name);
			TextView text_path = (TextView) v
					.findViewById(R.id.project_list_item_path);
			ImageView iv = (ImageView) v.findViewById(R.id.imageViewStatus);

			text_name.setText(item.m_project_settings.m_name);
			text_path.setText(item.m_project_settings.m_path);

			// String pspath = map.ps.m_path;
			if (map != null) {
				if (!item.m_project_settings.m_path
						.equalsIgnoreCase(map.ps.m_path)) {
					iv.setImageBitmap(null);
				} else {
					text_name.setEnabled(false);
					text_name.setTextColor(Color.GRAY);
				}
			}

			/*
			 * text_name.setOnLongClickListener(new OnLongClickListener() {
			 * 
			 * public boolean onLongClick(View v) {
			 * if(item.m_project_settings.m_path != ps.m_path) { map.Clear();
			 * //String new_path = item.m_project_settings.m_path;
			 * LoadPro(item.m_project_settings.m_path); map.UpdateMap(); sp =
			 * getPreferences(MODE_PRIVATE); SharedPreferences.Editor editor =
			 * sp.edit(); editor.putString(SAVED_PATH,
			 * item.m_project_settings.m_path); editor.commit();
			 * projects_dialog.cancel(); return true; } return false; } });
			 */
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!item.m_project_settings.m_path.equals(map.ps.m_path)) {
						map.Clear();
						LoadPro(item.m_project_settings.m_path);
						map.UpdateMap();
						sp = getPreferences(MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						editor.putString(SAVED_PATH,
								item.m_project_settings.m_path);
						editor.apply();
						editor.commit();
						projects_dialog.cancel();
					}
				}
			});

			return v;
		}

		public ProjectsAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}
	}

	public class MarkersAdapter extends ArrayAdapter<MarkersAdapterItem> {
		@Override
		public View getView(int position, View convertView,
				final ViewGroup parent) {
			final MarkersAdapterItem item = getItem(position);
			View v = LayoutInflater.from(getContext()).inflate(
					R.layout.markers_list_item, null);
			TextView text_name = (TextView) v
					.findViewById(R.id.markers_list_item_text);
			ImageView iv = (ImageView) v.findViewById(R.id.imageViewDirection);
			text_name.setText(item.m_marker.m_name);
			if (map.ps.m_markers_source != null) {
				if (map.ps.m_markers_source.equalsIgnoreCase("layer")) {
					if (GIEditLayersKeeper.Instance().m_CurrentTarget != null) {
						if (item.m_marker.m_lat == ((GI_WktPoint) GIEditLayersKeeper
								.Instance().m_CurrentTarget).m_lat
								&& item.m_marker.m_lon == ((GI_WktPoint) GIEditLayersKeeper
										.Instance().m_CurrentTarget).m_lon) {
							iv.setVisibility(View.VISIBLE);
						}
					}
				}
			}

			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					markers_dialog.cancel();
					GILonLat new_center = new GILonLat(item.m_marker.m_lon,
							item.m_marker.m_lat);
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.root);
					if (m_marker_point == null) {
						m_marker_point = new GIControlFloating(parent
								.getContext());
						layout.addView(m_marker_point);
						m_marker_point.setMap(map);
					}
					m_marker_point.setLonLat(new_center);
					if (item.m_marker.m_diag != 0) {
						map.SetCenter(new_center, item.m_marker.m_diag);
					} else {
						map.SetCenter(GIProjection.ReprojectLonLat(new_center,
								GIProjection.WGS84(), map.Projection()));
					}
				}
			});
			if (map.ps.m_markers_source != null) {
				if (map.ps.m_markers_source.equalsIgnoreCase("layer")) {
					v.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {

							markers_dialog.cancel();
							GILonLat new_center = new GILonLat(
									item.m_marker.m_lon, item.m_marker.m_lat);
							GI_WktPoint poi = new GI_WktPoint(new_center);
							GIEditLayersKeeper.Instance().m_CurrentTarget = poi;
							//GILocator arr = new GILocator(poi);
							GIDirectionToPOIArrow arrow = new GIDirectionToPOIArrow(poi);
							GIEditLayersKeeper.Instance().LocatorView(poi);
							//GIEditLayersKeeper.Instance().AccurancyRangeView(true);
							return false;
						}
					});
				}
			}
			return v;
		}

		public MarkersAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}
	}

	public class AddressSearchAdapter extends
			ArrayAdapter<AddressSearchAdapterItem> {
		@Override
		public View getView(int position, View convertView,
				final ViewGroup parent) {
			final AddressSearchAdapterItem item = getItem(position);
			View v = LayoutInflater.from(getContext()).inflate(
					R.layout.markers_list_item, null);
			TextView text_name = (TextView) v
					.findViewById(R.id.markers_list_item_text);
			text_name.setText(item.m_name);
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if ((item.m_lon == 0) && (item.m_lat == 0)) {
						return;
					}
					markers_dialog.cancel();
					GILonLat new_center = new GILonLat(item.m_lon, item.m_lat);
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.root);
					if (m_marker_point == null) {
						m_marker_point = new GIControlFloating(parent
								.getContext());
						m_marker_point.setMap(map);
						m_marker_point.setRoot(layout);
					}
					m_marker_point.setLonLat(new_center);
					map.SetCenter(new_center, item.m_diag);
				}
			});
			return v;
		}

		public AddressSearchAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}
	}

	public class EditableLayersAdapter extends
			ArrayAdapter<EditableLayersAdapterItem> {
		@Override
		public View getView(int position, View convertView,
				final ViewGroup parent) {
			final EditableLayersAdapterItem item = getItem(position);
			View v = LayoutInflater.from(getContext()).inflate(
					R.layout.markers_list_item, null);
			TextView text_name = (TextView) v
					.findViewById(R.id.markers_list_item_text);

			text_name.setText(item.m_layer.getName());
			GIEditableSQLiteLayer gov;
			GIEditableLayer layer = (GIEditableLayer) item.m_layer;
			switch (layer.m_Status) {
			case UNEDITED: {
				text_name.setTextColor(Color.BLACK);
				break;
			}
			case EDITED: {
				text_name.setTextColor(Color.BLUE);
				break;
			}
			case UNSAVED: {
				text_name.setTextColor(Color.RED);
				break;
			}
			default: {
				text_name.setTextColor(Color.BLACK);
				break;
			}
			}

			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GIEditableLayer layer = (GIEditableLayer) item.m_layer;
					// GIEditLayersKeeper.Instance().setMap(map);
					GIEditLayersKeeper.Instance().StartEditing(layer);
					// layer.Serialize();
					editablelayers_dialog.cancel();
				}
			});

			return v;
		}

		public EditableLayersAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}
	}

	public void AddProjects(ArrayAdapter<ProjectsAdapterItem> adapter) {
		File dir = (Environment.getExternalStorageDirectory());
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				if (file.getName().endsWith(".pro")) {
					GIProjectProperties proj = new GIProjectProperties(
							file.getPath(), true);
					if (proj != null) {
						adapter.add(new ProjectsAdapterItem(proj));
					}
				}
			}
		}
	}

	public void AddMarkers(ArrayAdapter<MarkersAdapterItem> adapter) {
		if (map.ps.m_markers_source == null) {
			if (adapter.isEmpty()) {
				GIPList PList = new GIPList();
				PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_markers); // "/sdcard/"
				for (GIPList.GIMarker marker : PList.m_list) {
					adapter.add(new MarkersAdapterItem(marker));
				}
			}
		}
		if (map.ps.m_markers_source != null) {
			if (map.ps.m_markers_source.equalsIgnoreCase("file")) {
				if (adapter.isEmpty()) {
					GIPList PList = new GIPList();
					PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_markers);// "/sdcard/"
					for (GIPList.GIMarker marker : PList.m_list) {
						adapter.add(new MarkersAdapterItem(marker));
					}
				}
			}
			if (map.ps.m_markers_source.equalsIgnoreCase("layer")) {
				GIEditableLayer layer = null;
				for (GITuple tuple : map.m_layers.m_list) {
					if (tuple.layer.getName()
							.equalsIgnoreCase(map.ps.m_markers)) {
						layer = (GIEditableLayer) tuple.layer;
						break;
					}
				}
				if (layer != null) 
				{
					adapter.clear();
					GIPList list = new GIPList();
					for (GI_WktGeometry geom : layer.m_shapes) 
					{
						GI_WktPoint point = (GI_WktPoint) geom;
						if (point != null) 
						{
							GIPList.GIMarker marker = list.new GIMarker();
							if (geom.m_attributes.containsKey("Name")) 
							{
								marker.m_name = (String) geom.m_attributes.get("Name").m_value.toString();
							}
							else if (!geom.m_attributes.keySet().isEmpty()) 
							{
								marker.m_name = (String) geom.m_attributes.get(geom.m_attributes.keySet().toArray()[0]).m_value;
							}
							else
							{
								marker.m_name = String.valueOf(geom.m_ID);
							}
							marker.m_lon = point.m_lon;
							marker.m_lat = point.m_lat;
							marker.m_description = "";
							marker.m_image = "";
							marker.m_diag = 0;
							adapter.add(new MarkersAdapterItem(marker));
						}
					}
				}
			}
		}
	}

	public void AddEditableLayers(GIGroupLayer layer,
			ArrayAdapter<EditableLayersAdapterItem> adapter) {
		if (adapter.isEmpty()) {

			/*
			 * for(GIPropertiesLayerRef layer : map.ps.m_Edit.m_Entries) {
			 * adapter.add(new EditableLayersAdapterItem(layer)); }
			 */
			if (adapter.isEmpty()) {
				for (GIEditableLayer editable_layer : GIEditLayersKeeper
						.Instance().m_Layers) {
					adapter.add(new EditableLayersAdapterItem(editable_layer));
				}
			}
		}
	}

	public void zoomIn(View target) {
		map.ScaleMapBy(map.Center(), 1.5f);
	}

	public void zoomOut(View target) {
		map.ScaleMapBy(map.Center(), 0.66f);
	}

//	public void moveToPosition(View target) {
//		if (follow_button.isActivated()) {
//			follow_button.setActivated(false);
//		} else {
//			follow_button.setActivated(true);
//		}
//		if (null != m_location_listener.m_location)
//			map.SetCenter(m_location_listener.m_location);
//	}

	// Temporary substitution for a GILayer iterator
	public void add_layers(GIGroupLayer layer,
			ArrayAdapter<LayersAdapterItem> adapter) {
		for (GITuple tuple : layer.m_list) {
			if (GILayerType.LAYER_GROUP == tuple.layer.type_)
				add_layers((GIGroupLayer) tuple.layer, adapter);
			else {
				adapter.add(new LayersAdapterItem(tuple));
			}
		}
	}

	// == Layers Dialog ==
	/*
	 * Dialog is always shown under the caller button, if there is enough space.
	 * Vertical size expends depending on contents up to a fixed value. Some
	 * other styles issued positioning errors.
	 */
	public void layersDialogClicked(final View layers_button) {
		final int layers_dialog_max_height = getWindowManager().getDefaultDisplay().getHeight() / 2;
		layers_button.setActivated(true);

		final Dialog layers_dialog = new Dialog(this,
				R.style.Theme_layers_dialog);
		layers_dialog.setContentView(R.layout.layers_dialog);
		layers_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		layers_dialog.setCanceledOnTouchOutside(true);

		layers_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				layers_button.setActivated(false);
			}
		});

		// Place dialog under the button
		LayoutParams parameters = layers_dialog.getWindow().getAttributes();
		parameters.height = layers_dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		layers_button.getLocationOnScreen(button_location);

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX
				+ layers_button.getWidth() / 2;
		parameters.y = button_location[1] - screenCenterY
				+ layers_button.getHeight() + parameters.height / 2;

		layers_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView layers_list = (ListView) layers_dialog
				.findViewById(R.id.layers_list);
		LayersAdapter adapter = new LayersAdapter(this,
				R.layout.layers_list_item, R.id.layers_list_item_text);
		// TODO
		// add_layer_header
		/**/
		View header = getLayoutInflater().inflate(
				R.layout.add_layer_header_layout, null);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OpenFileDialog dlg = new OpenFileDialog(getApplicationContext());
				dlg.setIFolderItemListener(m_fileOpenListener);
				dlg.show(getFragmentManager(), "open_dlg");
			}
		});
		layers_list.addHeaderView(header);
		/**/
		ImageButton additional = (ImageButton) layers_dialog
				.findViewById(R.id.layers_additional_button);
		additional.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OpenFileDialog dlg = new OpenFileDialog(getApplicationContext());
				dlg.setIFolderItemListener(m_fileOpenListener);
				dlg.show(getFragmentManager(), "open_dlg");
				layers_dialog.dismiss();
			}
		});
		/**/
		add_layers((GIGroupLayer) map.m_layers, adapter);
		layers_list.setAdapter(adapter);
		layers_dialog.show();
	}

	// == Info Dialog ==
	/*
	 * Mostly copied from Layer_Dialog
	 */
	public void ProjectSelectorDialogClicked(final View button) {
		final int dialog_max_height = getWindowManager().getDefaultDisplay().getHeight() / 2;
		button.setActivated(true);

		projects_dialog = new Dialog(this, R.style.Theme_layers_dialog);
		projects_dialog.setContentView(R.layout.project_selector_dialog);
		projects_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		projects_dialog.setCanceledOnTouchOutside(true);

		projects_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				button.setActivated(false);
			}
		});

		// Place dialog under the button
		LayoutParams parameters = projects_dialog.getWindow().getAttributes();
		parameters.height = dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		button.getLocationOnScreen(button_location);

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX + button.getWidth()
				/ 2;
		parameters.y = button_location[1] - screenCenterY + button.getHeight()
				+ parameters.height / 2;

		projects_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView project_list = (ListView) projects_dialog
				.findViewById(R.id.projects_list);
		View header = getLayoutInflater().inflate(
				R.layout.project_list_management_item, null);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				projects_dialog.cancel();
				ru.tcgeo.gilib.projectmanagement.GIServer.Instance()
						.getPresenter().getDialog()
						.show(getFragmentManager(), "dialog");
			}
		});
		project_list.addHeaderView(header);
		ProjectsAdapter adapter = new ProjectsAdapter(this,
				R.layout.project_selector_list_item,
				R.id.project_list_item_path);
		AddProjects(adapter);
		project_list.setAdapter(adapter);
		projects_dialog.show();
	}

	public void ProjectSelectorDialog() {
		final int dialog_max_height = 420;

		projects_dialog = new Dialog(this, R.style.Theme_layers_dialog);
		projects_dialog.setContentView(R.layout.project_selector_dialog);
		projects_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		projects_dialog.setCanceledOnTouchOutside(true);

		// Place dialog under the button
		LayoutParams parameters = projects_dialog.getWindow().getAttributes();
		parameters.height = dialog_max_height; // Some hard-coded size

		int[] button_location = { 200, 10 };

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX;
		parameters.y = button_location[1] - screenCenterY;

		projects_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView project_list = (ListView) projects_dialog
				.findViewById(R.id.projects_list);
		View header = getLayoutInflater().inflate(
				R.layout.project_list_management_item, null);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				projects_dialog.cancel();
				ru.tcgeo.gilib.projectmanagement.GIServer.Instance()
						.getPresenter().getDialog()
						.show(getFragmentManager(), "dialog");
			}
		});
		project_list.addHeaderView(header);
		ProjectsAdapter adapter = new ProjectsAdapter(this,
				R.layout.project_selector_list_item,
				R.id.project_list_item_path);
		AddProjects(adapter);
		project_list.setAdapter(adapter);
		projects_dialog.show();
	}

	public void MarkersDialogClicked(final View button) {
		final int dialog_max_height = getWindowManager().getDefaultDisplay().getHeight() / 2;
		button.setActivated(true);
		markers_dialog = new Dialog(this, R.style.Theme_layers_dialog);
		markers_dialog.setContentView(R.layout.markers_dialog);
		markers_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		markers_dialog.setCanceledOnTouchOutside(true);

		markers_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				button.setActivated(false);
			}
		});

		// Place dialog under the button
		LayoutParams parameters = markers_dialog.getWindow().getAttributes();
		parameters.height = dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		button.getLocationOnScreen(button_location);

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX + button.getWidth()
				/ 2;
		parameters.y = button_location[1] - screenCenterY + button.getHeight()
				+ parameters.height / 2;

		markers_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView markers_list = (ListView) markers_dialog
				.findViewById(R.id.markers_list);
		MarkersAdapter adapter = new MarkersAdapter(this,
				R.layout.markers_list_item, R.id.markers_list_item_text);
		AddMarkers(adapter);
		markers_list.setAdapter(adapter);
		markers_dialog.show();
	}

	public void AddressSearchDialogClicked(final View text_edit) {
		final int dialog_max_height = 420;
		markers_dialog = new Dialog(this, R.style.Theme_layers_dialog);
		markers_dialog.setContentView(R.layout.markers_dialog);
		markers_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		markers_dialog.setCanceledOnTouchOutside(true);

		markers_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				text_edit.setActivated(false);
				text_edit.clearFocus();
			}
		});

		LayoutParams parameters = markers_dialog.getWindow().getAttributes();
		parameters.height = dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		text_edit.getLocationOnScreen(button_location);

		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX
				+ text_edit.getWidth() / 2;
		parameters.y = button_location[1] - screenCenterY
				+ text_edit.getHeight() + parameters.height / 2;

		markers_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView markers_list = (ListView) markers_dialog
				.findViewById(R.id.markers_list);
		AddressSearchAdapter adapter = new AddressSearchAdapter(this,
				R.layout.markers_list_item, R.id.markers_list_item_text);
		ArrayList<AddressSearchAdapterItem> array = new ArrayList<AddressSearchAdapterItem>();

		//
		EditText edit_search = (EditText) findViewById(R.id.search_text);
		String part = edit_search.getText().toString();
		String path = map.ps.m_search_file;
		//
		GISQLRequest sql = new GISQLRequest(part, "final", path, array);
		GIScriptExpression res = (GIScriptExpression) map.ps.m_scriptparser_search
				.Eval(sql);
		if (res != null) {
			if (res.Type() == GIScriptExpression.TYPE.error) {
				// Log.d("ScriptLogs",
				// ((GIScriptExpressionError)res).error_text);
			} else if (res.Type() == GIScriptExpression.TYPE.operation) {
				for (int i = 0; i < array.size(); i++) {
					adapter.add(array.get(i));
				}
			}
		}
		markers_list.setAdapter(adapter);
		markers_dialog.show();
	}

	public void EditableLayersDialogClicked(final View button) {
		final int dialog_max_height = getWindowManager().getDefaultDisplay().getHeight() / 2;
		button.setActivated(true);
		editablelayers_dialog = new Dialog(this, R.style.Theme_layers_dialog);
		editablelayers_dialog.setContentView(R.layout.markers_dialog);
		editablelayers_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		editablelayers_dialog.setCanceledOnTouchOutside(true);

		editablelayers_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				button.setActivated(false);
			}
		});

		// Place dialog under the button
		LayoutParams parameters = editablelayers_dialog.getWindow()
				.getAttributes();
		parameters.height = dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		button.getLocationOnScreen(button_location);

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX + button.getWidth()
				/ 2;
		parameters.y = button_location[1] - screenCenterY + button.getHeight()
				+ parameters.height / 2;

		editablelayers_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView markers_list = (ListView) editablelayers_dialog
				.findViewById(R.id.markers_list);
		/**/
		View header = getLayoutInflater().inflate(
				R.layout.editable_layers_stop_edit, null);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GIEditLayersKeeper.Instance().StopEditing();
				editablelayers_dialog.cancel();
			}
		});
		markers_list.addHeaderView(header);
		/**/
		EditableLayersAdapter adapter = new EditableLayersAdapter(this,
				R.layout.markers_list_item, R.id.markers_list_item_text);
		AddEditableLayers((GIGroupLayer) map.m_layers, adapter);
		// AddEditableLayers(adapter);
		markers_list.setAdapter(adapter);
		editablelayers_dialog.show();
	}

	public void GPSDialogClicked(final View button) {
		// GIEditLayersKeeper.Instance().setMap(map);
		GIEditLayersKeeper.Instance().GPSDialog();

	}
	
	public void CompassClicked(final View button) 
	{
		GIEditLayersKeeper.Instance().CompassView();
	}	

	public void on_stop_search(final View button) {
		EditText text_edit = (EditText) findViewById(R.id.search_text);
		text_edit.setText("");
		text_edit.setActivated(false);
		text_edit.clearFocus();
		if (m_marker_point != null) {
			m_marker_point.Remove();
			m_marker_point = null;
		}
	}

	public void LoadPro(String path) {
		map.ps = new GIProjectProperties(path);
		GIBounds temp = new GIBounds(map.ps.m_projection, map.ps.m_left,
				map.ps.m_top, map.ps.m_right, map.ps.m_bottom);
		map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
		touchControl.InitMap(map);
		GIPropertiesGroup current_group = map.ps.m_Group;
		GIEditLayersKeeper.Instance().ClearLayers();
		loadGroup(current_group);
		// touchControl.m_project_settings = map.ps;                                                                          
	}

	// private void loadGroup(ru.tcgeo.gilib.parser.GIPropertiesGroup
	// current_layer2)
	private void loadGroup(	ru.tcgeo.gilib.parser.GIPropertiesGroup current_layer2) 
	{
		for (ru.tcgeo.gilib.parser.GIPropertiesLayer current_layer : current_layer2.m_Entries) 
		{
			if (current_layer.m_type == GILayerType.VECTOR_LAYER) 
			{

				// ToDo look for additional vector Styles
				Paint fill = new Paint();
				Paint line = new Paint();
				// Paint paint = new Paint();
				for (GIColor color : current_layer.m_style.m_colors) 
				{
					if (color.m_description.equalsIgnoreCase("line")) 
					{
						if (color.m_name.equalsIgnoreCase("custom")) 
						{
							line.setARGB(color.m_alpha, color.m_red, color.m_green, color.m_blue);
						} 
						else
						{
							color.setFromName();
							line.setARGB(color.m_alpha, color.m_red, color.m_green, color.m_blue);
						}
						line.setStyle(Style.STROKE);
						line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
					} else if (color.m_description.equalsIgnoreCase("fill")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
						fill.setStyle(Style.FILL);
					}
				}
				GILayer layer;

				if (current_layer.m_style.m_type.equalsIgnoreCase("vector")) {

					GIVectorStyle vstyle = new GIVectorStyle(line, fill,
							(int) current_layer2.m_opacity);

					if (current_layer.m_source.m_location
							.equalsIgnoreCase("local")) {

						layer = GILayer.CreateLayer(
								current_layer.m_source.GetLocalPath(),
								GILayerType.VECTOR_LAYER, vstyle,
								current_layer.m_encoding);
						if (layer == null) {
							continue;
						}
						layer.setName(current_layer.m_name);
						layer.m_layer_properties = current_layer;
						map.AddLayer(layer, new GIScaleRange(
								current_layer.m_range), current_layer.m_enabled);

					} else {
						continue;
					}
				}
				if (current_layer.m_style.m_type.equalsIgnoreCase("image")) {
					// ToDo add bitmap recourse/path here
					GIVectorStyle vstyle = new GIVectorStyle(
							BitmapFactory.decodeResource(getResources(),
									R.drawable.metro_icon));
					if (current_layer.m_source.m_location
							.equalsIgnoreCase("local")) {
						// ToDo add Encoding from XML here
						// layer =
						// GILayer.CreateLayer(current_layer.m_source.GetLocalPath(),
						// GILayerType.VECTOR_LAYER, vstyle, new
						// GIEncoding("CP1251"));
						layer = GILayer.CreateLayer(
								current_layer.m_source.GetLocalPath(),
								GILayerType.VECTOR_LAYER, vstyle,
								current_layer.m_encoding);
						layer.setName(current_layer.m_name);
						layer.m_layer_properties = current_layer;
						map.AddLayer(layer, new GIScaleRange(
								current_layer.m_range), current_layer.m_enabled);

					} else {
						continue;
					}
				}

			}
			if (current_layer.m_type == GILayerType.RASTER_LAYER) {
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetLocalPath(),
							GILayerType.RASTER_LAYER);
					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
					// map.AddLayer(layer);

				} else {
					continue;
				}

			}
			if (current_layer.m_type == GILayerType.LAYER_GROUP) {
				loadGroup((GIPropertiesGroup) current_layer);
			}
			if (current_layer.m_type == GILayerType.TILE_LAYER) {
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetLocalPath(),
							GILayerType.TILE_LAYER);
					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;
					// map.AddLayer(layer);
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
				} else {
					continue;
				}

			}
			if (current_layer.m_type == GILayerType.ON_LINE) {
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("text")) {
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetRemotePath(),
							GILayerType.ON_LINE);
					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;
					// map.AddLayer(layer);
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
				} else {
					continue;
				}

			}
			if (current_layer.m_type == GILayerType.SQL_LAYER) 
			{
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("text")) 
				{
					layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayerType.SQL_LAYER);
					layer.setName(current_layer.m_name);
					if (current_layer.m_sqldb != null) {
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("SMART")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.SMART;
							((GISQLLayer) layer).m_max_z = current_layer.m_sqldb.m_max_z;
							((GISQLLayer) layer).m_min_z = current_layer.m_sqldb.m_min_z;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("AUTO")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.AUTO;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.ADAPTIVE;
							((GISQLLayer) layer).getAvalibleLevels();
						}
					}
					layer.m_layer_properties = current_layer;
					map.AddLayer(layer,	new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				} 
				else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
				{
					layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayerType.SQL_LAYER);
					layer.setName(current_layer.m_name);
					if (current_layer.m_sqldb != null) {
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("SMART")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.SMART;
							((GISQLLayer) layer).m_max_z = current_layer.m_sqldb.m_max_z;
							((GISQLLayer) layer).m_min_z = current_layer.m_sqldb.m_min_z;
						}
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("AUTO")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.AUTO;
						}
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("ADAPTIVE")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.ADAPTIVE;
							((GISQLLayer) layer).getAvalibleLevels();
						}
					}
					layer.m_layer_properties = current_layer;
					// map.AddLayer(layer);
					map.AddLayer(layer,	new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				}
				else
				{
					continue;
				}

			}
			if (current_layer.m_type == GILayerType.SQL_YANDEX_LAYER) {
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("text")) 
				{
					layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayerType.SQL_YANDEX_LAYER);
					layer.setName(current_layer.m_name);
					if (current_layer.m_sqldb != null) {
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("SMART")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.SMART;
							((GISQLLayer) layer).m_max_z = current_layer.m_sqldb.m_max_z;
							((GISQLLayer) layer).m_min_z = current_layer.m_sqldb.m_min_z;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("AUTO")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.AUTO;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.ADAPTIVE;
							((GISQLLayer) layer).getAvalibleLevels();
						}
					}
					layer.m_layer_properties = current_layer;
					map.AddLayer(layer,	new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				} 
				else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
				{
					layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayerType.SQL_YANDEX_LAYER);
					layer.setName(current_layer.m_name);
					if (current_layer.m_sqldb != null) {
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("SMART")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.SMART;
							((GISQLLayer) layer).m_max_z = current_layer.m_sqldb.m_max_z;
							((GISQLLayer) layer).m_min_z = current_layer.m_sqldb.m_min_z;
						}
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("AUTO")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.AUTO;
						}
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("ADAPTIVE")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.ADAPTIVE;
							((GISQLLayer) layer).getAvalibleLevels();
						}
					}
					layer.m_layer_properties = current_layer;
					// map.AddLayer(layer);
					map.AddLayer(layer,	new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				}
				else
				{
					continue;
				}

			}			
			if (current_layer.m_type == GILayerType.DBASE) {
				Paint fill = new Paint();
				Paint line = new Paint();
				for (GIColor color : current_layer.m_style.m_colors) {
					if (color.m_description.equalsIgnoreCase("line")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						line.setStyle(Style.STROKE);
						line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
					} else if (color.m_description.equalsIgnoreCase("fill")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
						fill.setStyle(Style.FILL);
					}
				}

				Paint editing_fill = new Paint();
				editing_fill.setColor(Color.CYAN);
				editing_fill.setAlpha(96);
				editing_fill.setStyle(Style.FILL);

				Paint editing_stroke = new Paint();
				editing_stroke.setColor(Color.CYAN);
				editing_stroke.setStrokeWidth(2);
				editing_fill.setAlpha(128);
				editing_stroke.setStyle(Style.STROKE);
				GIVectorStyle vstyle_editing = new GIVectorStyle(
						editing_stroke, editing_fill,
						(int) current_layer2.m_opacity);

				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					GIVectorStyle vstyle = new GIVectorStyle(line, fill,
							(int) current_layer2.m_opacity);
					layer = GILayer
							.CreateLayer(current_layer.m_source.GetLocalPath(),
									GILayerType.DBASE, vstyle,
									current_layer.m_encoding);

					layer.setName(current_layer.m_name);

					layer.m_layer_properties = current_layer;
					layer.AddStyle(vstyle_editing);
					/**/
					for (GIPropertiesLayerRef ref : map.ps.m_Edit.m_Entries) {
						if (ref.m_name.equalsIgnoreCase(current_layer.m_name)) {
							GIEditableSQLiteLayer l = (GIEditableSQLiteLayer) layer;
							if (ref.m_type.equalsIgnoreCase("POINT")) {
								l.setType(GIEditableLayerType.POINT);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("LINE")) {
								l.setType(GIEditableLayerType.LINE);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("POLYGON")) {
								l.setType(GIEditableLayerType.POLYGON);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("TRACK")) {
								l.setType(GIEditableLayerType.TRACK);
								continue;
							}
						}
					}
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
					GIEditLayersKeeper.Instance().AddLayer(
							(GIEditableSQLiteLayer) layer);
				}

				else {
					continue;
				}
			}
			//
			if (current_layer.m_type == GILayerType.XML) {
				Paint fill = new Paint();
				Paint line = new Paint();
				for (GIColor color : current_layer.m_style.m_colors) {
					if (color.m_description.equalsIgnoreCase("line")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						line.setStyle(Style.STROKE);
						line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
					} else if (color.m_description.equalsIgnoreCase("fill")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
						fill.setStyle(Style.FILL);
					}
				}

				Paint editing_fill = new Paint();
				editing_fill.setColor(Color.CYAN);
				editing_fill.setAlpha(96);
				editing_fill.setStyle(Style.FILL);

				Paint editing_stroke = new Paint();
				editing_stroke.setColor(Color.CYAN);
				editing_stroke.setStrokeWidth(2);
				editing_fill.setAlpha(128);
				editing_stroke.setStyle(Style.STROKE);
				GIVectorStyle vstyle_editing = new GIVectorStyle(
						editing_stroke, editing_fill,
						(int) current_layer2.m_opacity);

				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					GIVectorStyle vstyle = new GIVectorStyle(line, fill,
							(int) current_layer2.m_opacity);
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetLocalPath(),
							GILayerType.XML, vstyle, current_layer.m_encoding);

					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;

					layer.AddStyle(vstyle_editing);
					/**/
					for (GIPropertiesLayerRef ref : map.ps.m_Edit.m_Entries) {
						if (ref.m_name.equalsIgnoreCase(current_layer.m_name)) {
							GIEditableLayer l = (GIEditableLayer) layer;
							if (ref.m_type.equalsIgnoreCase("POINT")) {
								l.setType(GIEditableLayerType.POINT);
								GIEditLayersKeeper.Instance().m_POILayer = l;
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("LINE")) {
								l.setType(GIEditableLayerType.LINE);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("POLYGON")) {
								l.setType(GIEditableLayerType.POLYGON);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("TRACK")) {
								GIEditLayersKeeper.Instance().m_TrackLayer = l;
								l.setType(GIEditableLayerType.TRACK);
								continue;
							}
						}
					}
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
					GIEditLayersKeeper.Instance().AddLayer(
							(GIEditableLayer) layer);
				}

				else {
					continue;
				}
			}
			//TODO
			if (current_layer.m_type == GILayerType.PLIST) 
			{
				Paint fill = new Paint();
				Paint line = new Paint();
				for (GIColor color : current_layer.m_style.m_colors) {
					if (color.m_description.equalsIgnoreCase("line")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						line.setStyle(Style.STROKE);
						line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
					} else if (color.m_description.equalsIgnoreCase("fill")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
						fill.setStyle(Style.FILL);
					}
				}


				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					GIVectorStyle vstyle = new GIVectorStyle(line, fill,
							(int) current_layer2.m_opacity);
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetLocalPath(),
							GILayerType.PLIST, vstyle, current_layer.m_encoding);

					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;

					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
					GIEditLayersKeeper.Instance().AddLayer(
							(GIEditableLayer) layer);
				}
			}

		}
	}

	public void onRuleTool(View target) {
		if (rule_button.isActivated()) {
			rule_button.setActivated(false);
			GIRuleToolControl.Instance(this, map).Disable();
		} else {
			rule_button.setActivated(true);
			square_button.setActivated(false);
			GISquareToolControl.Instance(this, map).Disable();

		}
		GITouchControl tc = (GITouchControl) findViewById(R.id.touchcontrol);
		tc.SetMeasureState(rule_button.isActivated(),
				square_button.isActivated());
	}

	public void onSquareTool(View target) {
		if (square_button.isActivated()) {
			square_button.setActivated(false);
			GISquareToolControl.Instance(this, map).Disable();
		} else {
			square_button.setActivated(true);
			rule_button.setActivated(false);
			GIRuleToolControl.Instance(this, map).Disable();
		}
		GITouchControl tc = (GITouchControl) findViewById(R.id.touchcontrol);
		tc.SetMeasureState(rule_button.isActivated(),
				square_button.isActivated());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GIEditLayersKeeper.Instance().setContext(this);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);
		// ?????
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);

		touchControl = (GITouchControl) findViewById(R.id.touchcontrol);
		
		// map = (GIMap)findViewById(R.id.map);
		m_gps_button = (GIGPSButtonView)findViewById(R.id.top_bar_gps_button);
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) 
		{
			//View
			m_top_bar = (FrameLayout) findViewById(R.id.top_bar_layout);
			//m_top_bar = (View) findViewById(R.id.horizontalScrollView2);
			//horizontalScrollView2
			findViewById(R.id.slide_button).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) 
						{
							if(m_top_bar.getVisibility() == View.VISIBLE)
							{
								m_top_bar.setVisibility(View.GONE);
							}
							else
							{
								m_top_bar.setVisibility(View.VISIBLE);
							}
//							if (m_top_bar.getWidth() > 10) 
//							{
//								RelativeLayout.LayoutParams m_param;
//								m_param = new RelativeLayout.LayoutParams(0, 0);
//								m_param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//								m_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//								m_top_bar.setLayoutParams(m_param);
//							} 
//							else 
//							{
//								RelativeLayout.LayoutParams m_param;
//								m_param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//								m_param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//								m_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//								m_top_bar.setLayoutParams(m_param);
//							}
						}
					});
			/*View surface_button = (View)findViewById(R.id.compass_surface_button);
			surface_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CompassClicked(v);					
				}
			});*/

			m_gps_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					GPSDialogClicked(v);					
				}
			});
		}
		createMap();

		GIEditLayersKeeper.Instance().setFragmentManager(getFragmentManager());
		GIEditLayersKeeper.Instance().setTouchControl(touchControl);
		GIEditLayersKeeper.Instance().setMap(map);
		GIEditLayersKeeper.Instance().setRoot(R.id.root);

		// Setup pixel size to let scale work properly
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double screenPixels = Math.hypot(dm.widthPixels, dm.heightPixels);
		double screenInches = Math.hypot(dm.widthPixels / dm.xdpi,
				dm.heightPixels / dm.ydpi);
		GIMap.inches_per_pixel = screenInches / screenPixels;
		


		// Listen to GPS
//		m_location_manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		m_location_listener = new GPSLocationListener();
		/**/
		//TODO test provider
//		String mocLocationProvider = LocationManager.GPS_PROVIDER;
//		m_location_manager.addTestProvider(mocLocationProvider, false, false, false, false, true, true, true, 0, 5);
//		m_location_manager.setTestProviderEnabled(mocLocationProvider, true);
//		m_location_manager.requestLocationUpdates(mocLocationProvider, 0, 0, m_location_listener);
//		
//
//		GI_WktLinestring track = (GI_WktLinestring) GIWKTParser.CreateGeometryFromWKT("LINESTRING(37.559085280886386 55.80221944939844, 37.55960621469864 55.80165563337241, 37.5601063111584 55.8011035555688, 37.56075226908558 55.80050448352692, 37.56198167288249 55.800868626459504, 37.563294426089364 55.801174034024015, 37.56448215518129 55.801420707608315, 37.56416959489393 55.80226643369717, 37.56341945020429 55.80301817473325, 37.562752654924616 55.80361720793568, 37.561148178782894 55.8031708703501, 37.559647889403614 55.80267754388229, 37.5584393229592 55.80206675003432, 37.557376617982214 55.801491185487016, 37.556251400947765 55.80092735887029, 37.555563768315594 55.80097434473485, 37.554813623625954 55.801526424378324, 37.553813430706434 55.80244262431031, 37.553313334246674 55.80280674904012, 37.55262570161451 55.803382294057506, 37.551792207514914 55.80386386597578, 37.550937876062825 55.80463906674227, 37.549979357848294 55.80418099543566, 37.5484999058215 55.80353498824023, 37.54616612234264 55.80261881412274, 37.547270502024595 55.80178484193591, 37.54841655641155 55.80090386591665, 37.54979182167588 55.80048099031706, 37.552188117212225 55.80041051060207, 37.55441771392865 55.800833386970346, 37.55668898535006 55.80228992582518, 37.55843932295921 55.803100395524744)");
//		new MockLocationProvider(m_location_manager, mocLocationProvider, track).start();
		/**/
		
		//TODO uncomment
//		m_location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	5, 5, m_location_listener);
//		m_location_manager.requestLocationUpdates(	LocationManager.NETWORK_PROVIDER, 5, 5, m_location_listener);
		
		m_location_listener = new GIGPSLocationListener(map);
		GIEditLayersKeeper.Instance().m_location_manager = m_location_listener.m_location_manager;
		
		m_gps_button.SetGPSEnabledStatus(m_location_listener.m_location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER));				
		//GIEditLayersKeeper.Instance().UpdateGPSButton();
		

		// Set tint for position button, can't do through xml
		follow_button = (ImageButton) findViewById(R.id.button_position);
		follow_button.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					((ImageButton) v).setColorFilter(0x99000000);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					((ImageButton) v).setColorFilter(Color.TRANSPARENT);
				}
				return false;
			}
		});

		EditText search_text = (EditText) findViewById(R.id.search_text);
		search_text.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						AddressSearchDialogClicked(v);
						return true;
					}
				}
				return false;
			}
		});

		rule_button = (ImageButton) findViewById(R.id.toggleButtonRule);

		square_button = (ImageButton) findViewById(R.id.toggleButtonSquare);

		GIScaleControl m_scale_control_fixed = (GIScaleControl) findViewById(R.id.scale_control_screen);
		m_scale_control_fixed.setMap(map);
		
//		GILonLat point_geo = new GILonLat(37.2108234372116, 55.64510105577884);
//		GILonLat point_m = GIProjection.ReprojectLonLat(point_geo, GIProjection.WGS84(), GIProjection.WorldMercator());
//		GILonLat point_merc_ = GIYandexUtils.GeoToMercator(point_geo);
//		GILonLat point_geo_ = GIYandexUtils.MercatorToGeo(point_m);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	// ToDo
	@Override
	protected void onResume() {
		super.onResume();
		GIEditLayersKeeper.Instance().onResume();
		m_gps_button.onResume();
	};

	// ToDo
	@Override
	protected void onStop() {
		super.onStop();
		// GIEditLayersKeeper.Instance().m_position = null;
	};

	// ToDo
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// GIEditLayersKeeper.Instance().m_position = null;
	};

	// ToDo
	@Override
	protected void onPause() {
		super.onPause();
		GIEditLayersKeeper.Instance().onPause();
		m_gps_button.onPause();
		// GIEditLayersKeeper.Instance().m_position = null;
		map.Synhronize();
		String SaveAsPath = "01000110.pro";
		if (map.ps.m_SaveAs != null) {
			if (map.ps.m_SaveAs.length() > 0) {
				SaveAsPath = map.ps.m_SaveAs;
			}
		}
		map.ps.SavePro(SaveAsPath);
	};

	//

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		GIBounds bounds = new GIBounds(GIProjection.WorldMercator(),
				savedInstanceState.getFloat("b_left"),
				savedInstanceState.getFloat("b_top"),
				savedInstanceState.getFloat("b_right"),
				savedInstanceState.getFloat("b_bottom"));

		map.InitBounds(bounds);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO: For now layers are re-created. They should be re-used.
		outState.putFloat("b_left", (float) map.Bounds().left());
		outState.putFloat("b_top", (float) map.Bounds().top());
		outState.putFloat("b_right", (float) map.Bounds().right());
		outState.putFloat("b_bottom", (float) map.Bounds().bottom());
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		ImageView top_bar = (ImageView) findViewById(R.id.top_bar);
		if (top_bar != null) {
			top_bar.setImageDrawable(getResources().getDrawable(
					R.drawable.top_bar));
		}
		View edittext = (View) findViewById(R.id.search_text);

		Bitmap bkg = ((BitmapDrawable) getResources().getDrawable(
				R.drawable.searchbar_background)).getBitmap();
		BitmapDrawable bkgbt = new BitmapDrawable(getResources(), bkg);
		edittext.setBackgroundDrawable((Drawable) bkgbt);
	}

	private boolean createMap() {
		map = (GIMap) findViewById(R.id.map);
		View parent = findViewById(R.id.root);
		parent.setBackgroundColor(Color.WHITE);
		sp = getPreferences(MODE_PRIVATE);
		String path = sp.getString(SAVED_PATH,
				getResources().getString(R.string.default_project_path));
		// TODO
		try {
			LoadPro(path);
			return true;
		} catch (Exception e) {

			// ProjectSelectorDialog();
			GIBounds temp = new GIBounds(GIProjection.WGS84(), 0, 90, 90, 0);
			map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
			map.ps = new GIProjectProperties();
			sp = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(SAVED_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_SaveAs);
			editor.apply();
			editor.commit();
			//String path_ = sp.getString(SAVED_PATH,	getResources().getString(R.string.default_project_path));
			touchControl.InitMap(map);
			return false;
		}

	}

	@Override
	public void OnCannotFileRead(File file) {
		Toast.makeText(getApplicationContext(), "can't be read!",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void OnFileClicked(File file) {
		addLayer(file);
	}

	public void addLayer(File file) {
		String filenameArray[] = file.getName().split("\\.");
		String extention = filenameArray[filenameArray.length - 1];
		if (extention.equalsIgnoreCase("sqlitedb")) 
		{
			GIPropertiesLayer properties_layer = new GIPropertiesLayer();
			properties_layer.m_enabled = true;
			properties_layer.m_name = file.getName();
			properties_layer.m_range = new GIRange();
			properties_layer.m_source = new GISource("absolute", file.getAbsolutePath()); //getName()
			properties_layer.m_type = GILayerType.SQL_LAYER;
			properties_layer.m_strType = "SQL_LAYER";
			GILayer layer;
			//TODO
			layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayerType.SQL_LAYER);
			//layer = GILayer.CreateLayer(file.getName(), GILayerType.SQL_LAYER);
			properties_layer.m_sqldb = new GISQLDB();//"auto";
			properties_layer.m_sqldb.m_zoom_type = "auto";

			properties_layer.m_sqldb.m_min_z = ((GISQLLayer)layer).m_min;
			properties_layer.m_sqldb.m_max_z = ((GISQLLayer)layer).m_max;
			((GISQLLayer)layer).m_min_z = ((GISQLLayer)layer).m_min;
			((GISQLLayer)layer).m_max_z = ((GISQLLayer)layer).m_max;
			int min = ((GISQLLayer)layer).m_min;
			int max = ((GISQLLayer)layer).m_max;
//			if(min > 0)
//			{
//				min = min - 1;
//			}
			
			properties_layer.m_range = new GIRange();
			double con = 0.0254*0.0066*256/(0.5*40000000);
			properties_layer.m_range.m_from = (int)( 1/(Math.pow(2,  min)*con));
			properties_layer.m_range.m_to =  (int) ( 1/(Math.pow(2,  max)*con));

			map.ps.m_Group.addEntry(properties_layer);
			layer.setName(file.getName());
			layer.m_layer_properties = properties_layer;
			map.InsertLayerAt(layer, 0);
		}
		//else if(extention.equalsIgnoreCase("xml"))
		map.UpdateMap();
	}

//	static {
//		System.loadLibrary("geos-3.3.6");
//		System.loadLibrary("geos_c-1.7.6");
//		System.loadLibrary("gilib-native");
//	}
}
