<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes"
		cdata-section-elements="title record-field-value mime-type status" />


	<xsl:template match="directory">
		<directory id="{@id}">
			<title>
				<xsl:value-of select="title" />
			</title>
			<creation-date>
				<xsl:value-of select="creation-date" />
			</creation-date>
			<list-entry>
				<xsl:apply-templates select="list-entry/entry" />
				<xsl:apply-templates select="list-entry/status" />
			</list-entry>
			<list-record>
				<xsl:apply-templates select="list-record/record" />
			</list-record>
		</directory>
	</xsl:template>



	<xsl:template match="record">
		<record id="{@id}">
			<creation-date>
				<xsl:value-of select="creation-date" />
			</creation-date>
			<status>
				<xsl:value-of select="status" />
			</status>
			<list-entry>
				<xsl:apply-templates select="list-entry/entry" />
			</list-entry>
		</record>
	</xsl:template>


	<xsl:template match="record/list-entry/entry">
		<entry id="{@id}">
			<list-record-field>
				<xsl:apply-templates select="list-record-field/record-field" />
			</list-record-field>
		</entry>
	</xsl:template>
	
	<xsl:template match="entry">
		<entry id="{@id}">
			<title>
				<xsl:value-of select="title" />
			</title>
		</entry>
	</xsl:template>
	

	<xsl:template match="record-field">
		<record-field-value>
			<xsl:value-of select="record-field-value" />
		</record-field-value>
		<xsl:apply-templates select="file" />
	</xsl:template>

	<xsl:template match="file">
		<xsl:choose>
			<xsl:when test="count(child::*) != 0">
				<file id="{@id}">
					<title>
						<xsl:value-of select="title" />
					</title>
					<size>
						<xsl:value-of select="size" />
					</size>
					<mime-type>
						<xsl:value-of select="mime-type" />
					</mime-type>
				</file>

			</xsl:when>
			<xsl:otherwise>
				<file />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="status">
		<xsl:if test="@display='yes'">
                    <status>1</status>
		</xsl:if>
		<xsl:if test="@display='no'">
                    <status>0</status>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>