<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:param name="title-back-search" />
	<xsl:param name="label-back-search" />
	<xsl:param name="id-directory" />
	<xsl:param name="title-back-record" />
	<xsl:param name="label-back-record" />
	<xsl:param name="id-last-record" />
	<xsl:param name="id-last-directory" />
	<xsl:output method="html" indent="yes" />

	<xsl:template match="directory">
		<div>
			<xsl:apply-templates select="list-entry/entry" />
		</div>
		<div id="back-search">
			
			<xsl:variable name="var-id-last-directory" select="$id-last-directory"/>
			<xsl:if test="not(string(var-id-last-directory) = 'null')">
				<a href="jsp/site/Portal.jsp?page=directory&amp;id_directory={$id-last-directory}"
								title="{$title-back-search}" alt="{$title-back-search}">
							
						<xsl:value-of select="$label-back-search" />
				</a>
			</xsl:if>
			<br/>
			<xsl:variable name="var-id-last-record" select="$id-last-record"/>
			<xsl:if test="not(string(var-id-last-record) = 'null')">
				<a href="jsp/site/Portal.jsp?page=directory&amp;id_directory_record={$id-last-record}&amp;view_directory_record=1"
								title="{$title-back-record}" alt="{$title-back-record}">
							
						<xsl:value-of select="$label-back-record" />
				</a>
			</xsl:if>
		</div>

	</xsl:template>

	<xsl:template match="list-record-field">
		<xsl:apply-templates select="record-field" />
	</xsl:template>

	<xsl:template match="entry">
    <xsl:choose>
        <xsl:when test="not(count(./list-entry)=0)">
           <div class="prepend-1 append-1">  
              
              <fieldset class="form-fieldset"> 
	    					 <legend class="form-legend"> <xsl:value-of select="title" /> </legend>
                  <xsl:apply-templates select="./list-entry/entry" />
              </fieldset> 
             </div> 
          </xsl:when>
    	<xsl:otherwise> 
           <div class="entry-element">
        		    	<span class="entry-label">
        			     	<label>
        				      	<xsl:value-of select="title" />:
        			     	</label>
        		     	</span>
        			     <div class="entry-value">
        			          <xsl:apply-templates select="//list-record/record/list-entry/entry[@id=current()/@id]/list-record-field" />
        		    	 </div>
        	   	</div>
    	   	 </xsl:otherwise>
        </xsl:choose>  
</xsl:template>

	<xsl:template match="record-field">
		<span>
			<xsl:choose>
				<xsl:when test="not(string(record-field-value)='')">
					<xsl:value-of disable-output-escaping="yes"
						select="record-field-value" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&#160;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="file" />
		</span>
	</xsl:template>

	<xsl:template match="file">
		<xsl:variable name="id" select="@id" />
		<xsl:variable name="type" select="@type-entry" />
		<xsl:variable name="height" select="./height" />
		<xsl:variable name="width" select="./width" />
		<xsl:if test="child::node()">
			<xsl:choose>
				<xsl:when test="$type='10'">
					<xsl:choose>
						<xsl:when test="$height!='-1' and $width!='-1' ">
							<img src="image?resource_type=directory_entry_img&amp;id={$id}" height="{$height}" width="{$width}"  />
						</xsl:when>
						<xsl:when test="$height='-1' and $width!='-1'">
							<img src="image?resource_type=directory_entry_img&amp;id={$id}" width="{$width}"  />
						</xsl:when>
						<xsl:when test="$height!='-1' and $width='-1'">
							<img src="image?resource_type=directory_entry_img&amp;id={$id}" height="{$height}" />
						</xsl:when>
						<xsl:otherwise>
							<img src="image?resource_type=directory_entry_img&amp;id={$id}"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<a  href="jsp/site/plugins/directory/DoDownloadFile.jsp?id_file={$id}">
					<xsl:value-of disable-output-escaping="yes"
						select="title" />( <xsl:value-of disable-output-escaping="yes"
						select="size" /> O)
					</a>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<br />
	</xsl:template>
</xsl:stylesheet>


