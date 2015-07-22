package org.bitstrings.maven.plugins.indexer;

import java.io.File;

public class Index
{
    private String indexFileName;
    private Boolean forceIndexing;
    private File directory;
    private Boolean recursive;
    private String fileIncludes;
    private String fileExcludes;

    public String getIndexFileName()
    {
        return indexFileName;
    }

    public void setIndexFileName( String indexFileName )
    {
        this.indexFileName = indexFileName;
    }

    public Boolean isForceIndexing()
    {
        return forceIndexing;
    }

    public void setForceIndexing( Boolean forceIndexing )
    {
        this.forceIndexing = forceIndexing;
    }

    public File getDirectory()
    {
        return directory;
    }

    public void setDirectory( File directory )
    {
        this.directory = directory;
    }

    public Boolean isRecursive()
    {
        return recursive;
    }

    public void setRecursive( Boolean recursive )
    {
        this.recursive = recursive;
    }

    public String getFileIncludes()
    {
        return fileIncludes;
    }

    public void setFileIncludes( String fileIncludes )
    {
        this.fileIncludes = fileIncludes;
    }

    public String getFileExcludes()
    {
        return fileExcludes;
    }

    public void setFileExcludes( String fileExcludes )
    {
        this.fileExcludes = fileExcludes;
    }
}
