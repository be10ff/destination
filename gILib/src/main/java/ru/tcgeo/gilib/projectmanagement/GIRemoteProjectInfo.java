package ru.tcgeo.gilib.projectmanagement;

import java.io.File;
import java.util.ArrayList;

import ru.tcgeo.gilib.R;
import ru.tcgeo.gilib.parser.GIPropertiesPackage;
import ru.tcgeo.gilib.projectmanagement.GIPresenter.ProjectInState;
import android.os.Environment;

public class GIRemoteProjectInfo extends GIProjectInfo 
{

	public GIRemoteProjectInfo(int id, String name, String file, GIProjectStatus status) 
	{
		super(id, name, file, status);
	}
	
	@Override
	public GIProjectStatus getStatus(GIServer server)
	{
		m_inState = ProjectInState.READY;
		File dir = (Environment.getExternalStorageDirectory());
		for(File file : dir.listFiles())
		{
			if(m_file.equalsIgnoreCase(file.getName()))
			{
				m_status = GIProjectStatus.UPTODATE;
				return m_status;
			} 
		}
		m_status = GIProjectStatus.FAIL;
		return m_status;
	}
	@Override
	public int getStatusImageId() {
		if(m_status == GIProjectInfo.GIProjectStatus.UPTODATE)
		{
			return R.drawable.project_mark;
		}
		if(m_status == GIProjectInfo.GIProjectStatus.FAIL)
		{
			return R.drawable.project_mark_absence;
		}
		if(m_status == GIProjectInfo.GIProjectStatus.STRANGE)
		{
			return R.drawable.project_mark_alert;
		}
		if(m_status == GIProjectInfo.GIProjectStatus.UNKNOWN)
		{
			return R.drawable.project_mark_alert;
		}
		return R.drawable.project_mark_alert;
	}
	@Override
	public ArrayList<GIPropertiesPackage> getProjectPackages() {
		return m_packets;
	}
}
