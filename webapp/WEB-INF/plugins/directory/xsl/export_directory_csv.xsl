<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text"/>

	<xsl:template match="directory">
		<xsl:apply-templates select="list-entry/entry"/>
		<xsl:apply-templates select="list-entry/status"/>
		<xsl:if test="count(//partialexport)=0" >
			<xsl:text>&#10;</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="list-record/record"/>
	</xsl:template>
	
	<xsl:template match="record">
		<xsl:apply-templates select="list-entry/entry/list-record-field"/>
		<xsl:if test="status = true()">
			<xsl:text>;"</xsl:text>
			<xsl:value-of disable-output-escaping="yes" select="status"/>
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:if test="count(//partialexport)=0" >
			<xsl:text>&#10;</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="list-record-field">
		<xsl:text>"</xsl:text>
		<xsl:apply-templates select="record-field"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>;</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="entry">
		<xsl:text>"</xsl:text>
		<xsl:value-of select="title"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="status">
		<xsl:if test="@display='yes'">
            <xsl:text>;"status";</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="record-field">
		<xsl:value-of disable-output-escaping="yes" select="record-field-value"/>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
