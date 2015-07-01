package ru.tcgeo.application.gilib;

import ru.tcgeo.gilib.*;
import ru.tcgeo.gilib.GIGeometry;
import ru.tcgeo.gilib.GILayer;
import ru.tcgeo.gilib.GILonLat;

public interface GIDataRequestor
{
	boolean needsHierarchicalView();

	ru.tcgeo.gilib.GIDataRequestor StartGatheringData(ru.tcgeo.gilib.GILonLat point);
	ru.tcgeo.gilib.GIDataRequestor EndGatheringData (GILonLat point);

	ru.tcgeo.gilib.GIDataRequestor StartHierarchyLevel();
	ru.tcgeo.gilib.GIDataRequestor EndHierarchyLevel();

	ru.tcgeo.gilib.GIDataRequestor StartLayer(GILayer layer);
	ru.tcgeo.gilib.GIDataRequestor EndLayer(GILayer layer);

	ru.tcgeo.gilib.GIDataRequestor StartObject(GIGeometry geometry);
	ru.tcgeo.gilib.GIDataRequestor EndObject(GIGeometry geometry);

	ru.tcgeo.gilib.GIDataRequestor ProcessSemantic(String name, String value);
}
