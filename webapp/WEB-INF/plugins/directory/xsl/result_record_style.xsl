<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:param name="title-back-search" />
	<xsl:param name="label-back-search" />
	<xsl:param name="id-directory" />>
	<xsl:param name="isExtendInstalled" />
	<xsl:output method="html" indent="yes" />

	<xsl:template match="directory">
		<xsl:if test="$isExtendInstalled = 'true'">
				<![CDATA[@ExtenderParameter[]]><xsl:value-of select="//list-record/record/@id" /><![CDATA[,DIRECTORY_RECORD,opengraph]@]]>
		   		<![CDATA[@Extender[]]><xsl:value-of select="//list-record/record/@id" /><![CDATA[,DIRECTORY_RECORD,actionbar]@]]>
				<![CDATA[@Extender[]]><xsl:value-of select="//list-record/record/@id" /><![CDATA[,DIRECTORY_RECORD,hit,{show:true}]@]]>
				<![CDATA[@Extender[]]><xsl:value-of select="//list-record/record/@id" /><![CDATA[,DIRECTORY_RECORD,rating,{show:"all"}]@]]>
				<![CDATA[@Extender[]]><xsl:value-of select="//list-record/record/@id" /><![CDATA[,DIRECTORY_RECORD,opengraph,{footer:false, header:false}]@]]>
		</xsl:if>
		<div>
			<xsl:apply-templates select="list-entry/entry" />
		</div>
		<div id="back-search">
			<a href="jsp/site/Portal.jsp?page=directory&amp;id_directory={$id-directory}"
							title="{$title-back-search}" alt="{$title-back-search}">
						
					<xsl:value-of select="$label-back-search" />
			</a>
		</div>
		<xsl:if test="$isExtendInstalled = 'true'">
			<![CDATA[@Extender[]]><xsl:value-of select="//list-record/record/@id" /><![CDATA[,DIRECTORY_RECORD,comment]@]]>
	   		<![CDATA[@Extender[]]><xsl:value-of select="//list-record/record/@id" /><![CDATA[,DIRECTORY_RECORD,feedback]@]]>
	   		<![CDATA[@Extender[]]><xsl:value-of select="//list-record/record/@id" /><![CDATA[,DIRECTORY_RECORD,opengraph,{footer:true}]@]]>
		</xsl:if>
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
		<xsl:variable name="type-recordfield" select="@type-entry" />
		<xsl:variable name="show" select="@isShownInRecord" />
		<span>
			<xsl:choose>
				<xsl:when test="$type-recordfield='10'">
					<xsl:text>&#160;</xsl:text>
				</xsl:when>
				<xsl:when test="not(string(record-field-value)='')">					
						<xsl:value-of disable-output-escaping="yes"
							select="record-field-value" />
				</xsl:when>		
				<xsl:otherwise>
					<xsl:text>&#160;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$type-recordfield='10' and $show='false'">
					<xsl:text>&#160;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="file" />
				</xsl:otherwise>
			</xsl:choose>
			
		</span>
	</xsl:template>
	
	<xsl:template match="list-record-field[@geolocation='true']">
		<input type="hidden">
			<xsl:attribute name="name"><xsl:value-of select="../@id" />_x</xsl:attribute>
			<xsl:attribute name="id"><xsl:value-of select="../@id" />_x</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="record-field[@title='X']" /></xsl:attribute>
		</input>
		
		<input type="hidden">
			<xsl:attribute name="name"><xsl:value-of select="../@id" />_y</xsl:attribute>
			<xsl:attribute name="id"><xsl:value-of select="../@id" />_y</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="record-field[@title='Y']" /></xsl:attribute>
		</input>
		
		<!-- if x and y should be displayed : 
			X : <xsl:value-of select="record-field[@title='X']" />
			Y : <xsl:value-of select="record-field[@title='Y']" />
		 -->
		Adresse : <xsl:value-of select="record-field[@title='address']" />
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


