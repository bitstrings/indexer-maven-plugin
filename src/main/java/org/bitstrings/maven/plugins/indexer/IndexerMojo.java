/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitstrings.maven.plugins.indexer;

import static com.google.common.base.Strings.*;
import static org.apache.maven.plugins.annotations.LifecyclePhase.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

@Mojo(
    name = "index",
    defaultPhase = PROCESS_RESOURCES,
    threadSafe = true,
    requiresProject = true,
    requiresOnline = false
)
public class IndexerMojo
    extends AbstractMojo
{
    private static final String[] DEFAULT_EXCLUDES = { ".svn", "_svn", ".git", "_git" };

    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject mavenProject;

    @Parameter( defaultValue = "false" )
    private boolean quiet;

    @Parameter( defaultValue = "index" )
    private String indexFileName;

    @Parameter
    private Index[] indexes;

    @Parameter( defaultValue = "false" )
    private boolean forceIndexing;

    @Parameter( defaultValue = "true" )
    private boolean recursive;

    @Parameter( defaultValue = "true" )
    private boolean addDefaultExcludes;

    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        for ( Index index : indexes )
        {
            initIndex( index );

            List<String> includes = convertCsvToList( index.getFileIncludes() );
            List<String> excludes = convertCsvToList( index.getFileExcludes() );

            if ( addDefaultExcludes )
            {
                Collections.addAll( excludes = new ArrayList( excludes ), DEFAULT_EXCLUDES );
            }

            createIndexFile(
                index.getIndexFileName(),
                index.getDirectory(),
                includes, excludes,
                index.isRecursive()
            );
        }
    }

    private void createIndexFile(
                    String indexFileName,
                    File directory,
                    List<String> includes, List<String> excludes,
                    boolean recursive )
        throws MojoExecutionException
    {
        final Collection<File> files =
            FileUtils.listFilesAndDirs(
                directory,
                FileFilterUtils
                    .and(
                        new WildcardFileFilter( includes ),
                        FileFilterUtils.notFileFilter( new WildcardFileFilter( excludes ) ) ),
                ( recursive ? TrueFileFilter.INSTANCE : null )
            );

        final File indexFile = new File( directory, indexFileName );

        final List<File> directories = Lists.newArrayList();

        try ( BufferedWriter writer = new BufferedWriter( new FileWriter( indexFile ) ) )
        {
            if ( !quiet )
            {
                getLog().info( "Writing index for [ " + indexFile + " ]." );
            }

            for ( File file : files )
            {
                if ( file.equals( directory ) )
                {
                    continue;
                }

                final StringBuilder sb = new StringBuilder();

                if ( file.isDirectory() )
                {
                    sb.append( "D" );
                    directories.add( file );
                }
                else
                {
                    sb.append( "F" );
                }

                sb.append( "," );
                sb.append( file.getName() );

                writer.write( sb.toString() );
                writer.newLine();
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getLocalizedMessage(), e );
        }

        if ( recursive )
        {
            for ( File dir : directories )
            {
                createIndexFile( indexFileName, dir, includes, excludes, recursive );
            }
        }
    }

    private void initIndex( Index index )
    {
        if ( index.getIndexFileName() == null )
        {
            index.setIndexFileName( indexFileName );
        }

        if ( index.isRecursive() == null )
        {
            index.setRecursive( recursive );
        }

        if ( index.isForceIndexing() == null )
        {
            index.setForceIndexing( forceIndexing );
        }

        if ( isNullOrEmpty( index.getFileIncludes() ) )
        {
            index.setFileIncludes( "*" );
        }
    }

    private List<String> convertCsvToList( String csv )
    {
        if ( csv == null )
        {
            return Collections.EMPTY_LIST;
        }

        return Splitter.on( ',' ).trimResults().omitEmptyStrings().splitToList( csv );
    }
}
