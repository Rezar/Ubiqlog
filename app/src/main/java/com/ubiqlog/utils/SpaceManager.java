package com.ubiqlog.utils;

import java.io.File;

public class SpaceManager 
{
	public static class CapacityHolder 
	{
		String stringCapacity;
		int iCapacity;
		int iFactor;
		boolean isParsed;
		
		public CapacityHolder(String stringCapacity)
		{
			this.stringCapacity = stringCapacity;
			this.isParsed = false;
		}
			
		public int getCapacity()
		{
			if (!isParsed)
			{
				Parse();
			}
			return iCapacity; 
		}
		
		public int getCapacityFactor()
		{
			if (!isParsed)
			{
				Parse();
			}
			return iFactor;
		}
		
		private void Parse()
		{
			String toLower = stringCapacity.toLowerCase();
			int index = toLower.lastIndexOf("mb");
			boolean unknownSyntax = false;
			
			if (index == -1)
			{
				index = toLower.lastIndexOf("kb");
				
				if (index == -1)
				{
					index = toLower.lastIndexOf("b");
					
					if (index == -1)
					{
						unknownSyntax  = true;
					}
					else
					{
						iFactor = 1;
					}
				}
				else
				{
					iFactor = 1000;
				}
			}
			else
			{
				iFactor = 1000000;
			}
			
			if (unknownSyntax)
			{
				iFactor = 1;
				iCapacity = 1;
			}
			else
			{
				try
				{
					iCapacity = Integer.parseInt(stringCapacity.substring(0, index).trim());
				}
				catch (Exception ex)
				{
					unknownSyntax = true;
				}
				
				if (unknownSyntax)
				{
					iFactor = 1;
					iCapacity = 1;
				}
			}
			
			isParsed = true;
		}
	}
	
	public static class DirectorySize 
	{
		String directory;
		
		public DirectorySize(String directory)
		{
			this.directory = directory;
		}
		
		private long parseDirectory(File directory)
		{
			File[] files = directory.listFiles();
			
			long dirSize = 0;
			
			for (int i = 0; i < files.length; ++i)
			{
				if (files[i].isDirectory())
				{
					dirSize += parseDirectory(files[i]);
				}
				else if (files[i].isFile())
				{
					dirSize += files[i].length();
				}
			}
			
			return dirSize;
		}
		
		public long getSize()
		{
			File dir;
			long size;
			
			try
			{
				dir = new File(directory);
				size = parseDirectory(dir);
			}
			catch (Exception ex)
			{
				size = 0;
			}
			
			return size;
		}
	}

}
