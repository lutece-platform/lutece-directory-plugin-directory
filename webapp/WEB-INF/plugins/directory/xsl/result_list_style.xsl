<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	 <xsl:param name="title-descriptive" />
	
	<xsl:output method="html" indent="yes" />

	<xsl:template match="directory">
	
		<table width="100%" name="results_list" id="results_list">
			<tr>
				<th>&#160;</th>
				<xsl:apply-templates select="list-entry/entry" />
				<xsl:apply-templates select="list-entry/status" />
			</tr>
			
			<xsl:apply-templates select="list-record/record"/>
		</table>
	</xsl:template>

	<xsl:template match="record">
		<tr>
			<td class="link-directory-record">
				<a href="jsp/site/Portal.jsp?page=directory&amp;id_directory_record={@id}&amp;view_directory_record=1">
					<img src="images/local/skin/buttons/b_search.png" title="{$title-descriptive}" alt="{$title-descriptive}" />
				</a>
			</td>
			<xsl:apply-templates select="list-entry/entry/list-record-field" />
			<td><xsl:value-of disable-output-escaping="yes"	select="status" /></td>
		</tr>
	</xsl:template>

	<xsl:template match="list-record-field">
		<xsl:choose>
			<xsl:when test="position()=1">
				<td class="first-record-field">
					<xsl:choose>
						<xsl:when test="@geolocation = 'true'">
							<xsl:call-template name="field-geolocalisation" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="record-field" />
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:when>
			<xsl:otherwise>  
				<td>
					<xsl:choose>
						<xsl:when test="@geolocation = 'true'">
							<xsl:call-template name="field-geolocalisation" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="record-field" />
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="entry">
		<xsl:variable name="id-directory" select="../../@id" />
		<th>
			<xsl:if test="@is-sortable = 'true'">
				<a id="sort" href="jsp/site/Portal.jsp?page=directory&amp;id_directory={$id-directory}&amp;sorted_attribute_name={@id}&amp;asc_sort=true#sort" >
					<img src="images/admin/skin/actions/sort_asc.gif" style="vertical-align: text-bottom;" alt="asc" title="asc" />
				</a>
				<a href="jsp/site/Portal.jsp?page=directory&amp;id_directory={$id-directory}&amp;sorted_attribute_name={@id}&amp;asc_sort=false#sort" >
	    			<img src="images/admin/skin/actions/sort_desc.gif" style="vertical-align: text-bottom;" alt="desc" title="desc" />
	    		</a>
	    	 </xsl:if>
			<xsl:value-of select="title" />
		</th>
	</xsl:template>

	<xsl:template match="record-field">	
	
		<xsl:variable name="type-recordfield" select="@type-entry" />
		<xsl:variable name="show" select="@isShownInList" />
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
		<br />
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
	<xsl:template match="status">
		<xsl:choose>
			<xsl:when test="@display='yes'">
				<th>Statut</th>
			</xsl:when>
			<xsl:otherwise>
				<th>&#160;</th>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	
	<xsl:template name="field-geolocalisation">
		<input type="hidden" name="record_id">
			<xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="../../../@id"/></xsl:attribute>
		</input>
		<xsl:if test="../../../status/@icon &gt; 0">
			<input type="hidden" name="state_icon">
				<xsl:attribute name="value">image?resource_type=workflow_icon_img&amp;id=<xsl:value-of disable-output-escaping="yes" select="../../../status/@icon"/></xsl:attribute> 
			</input>
		</xsl:if>
		<input type="hidden" name="geolocation">
			<xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="../@id" /></xsl:attribute>
		</input>
		
		<input type="hidden" name="title">
			<xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="../../entry[position()=1]/list-record-field/record-field[position()=1]/record-field-value" /></xsl:attribute>
		</input>
		
		<input type="hidden">
			<xsl:attribute name="name">x</xsl:attribute>
			<xsl:attribute name="id">x</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="record-field[@title='X']" /></xsl:attribute>
		</input>
		
		<input type="hidden">
			<xsl:attribute name="name">y</xsl:attribute>
			<xsl:attribute name="id">y</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="record-field[@title='Y']" /></xsl:attribute>
		</input>
		<xsl:value-of disable-output-escaping="yes" select="record-field[@title='address']" />
	</xsl:template>
</xsl:stylesheet>


