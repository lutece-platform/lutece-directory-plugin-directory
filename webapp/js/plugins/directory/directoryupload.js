var uploading = 0;
var baseUrl = $( 'base' ).attr( 'href' );
function addAsynchronousUploadField(fieldId) {
	var flashVersion = swfobject.getFlashPlayerVersion();
	/* Flash Player 9.0.24 or greater  - simple mode otherwise */
	if ( swfobject.hasFlashPlayerVersion( "9.0.24" ) )
	{
		$("#_directory_upload_submit_" + fieldId).hide();
	    $('#' + fieldId).uploadify({
	        'uploader' : 'js/plugins/directory/uploadify/swf/uploadify.swf',
	        'script' : baseUrl + '/jsp/site/upload',
	        'cancelImg' : 'js/plugins/directory/uploadify/cancel.png',
			'auto' : true,
			'buttonText' : 'Parcourir',
			'displayData' : 'percentage',
			
			// file types & size limit
			'fileExt' : getFileExtValue( fieldId ),
			'fileDesc': ( getFileExtValue( fieldId ) == null ? null : 'Fichiers ' + "(" + getFileExtValue( fieldId ) ) + ")",
			'sizeLimit' : getMaxLengthValue( fieldId ),
			
			// additional parameters
			'scriptData' : {'jsessionid' : document.cookie.match(/JSESSIONID=([^;]+)/)[1], 'plugin_name': 'directory', 'field_name': fieldId},
			
			// event handlers
			'onComplete' : function(event,ID,fileObj,data) {
				directoryOnUploadComplete(event,ID,fileObj,data);
				$('#' + fieldId).uploadifySettings('hideButton',false);
			},
			'onError' : function(event,ID,fileObj,data) {
				handleError( event,ID,fileObj,data,fieldId );
				$('#' + fieldId).uploadifySettings('hideButton',false);
			},
			'onCancel' : function(event,ID,fileObj,data) {
				uploading--;
				$('#' + fieldId).uploadifySettings('hideButton',false);
			},
			'onSelect' : function(event,ID) {
				if ( !directoryStartUpload( event, ID, fieldId ) )
				{
					return false;
				}
				else
				{
					$( '#' + fieldId ).uploadifySettings('hideButton',true);
				}
			}
	    });
	    
	    $( '#_directory_upload_submit_' + fieldId ).hide(  );
	    if ( !$( '#_directory_upload_checkbox_' + fieldId + '0' ).length )
		{
			// no file uploaded, hiding content
			$("#_file_deletion_" + fieldId ).hide(  );
			$("#_file_deletion_label_" + fieldId ).hide(  );
			$("#_file_deletion_button_" + fieldId ).hide(  );
		}
	}
}

function canUploadFile( fieldId )
{
	// return true since onSelect does not work properly...
	return true;
	/* var filesCount = getUploadedFilesCount( fieldId );
	var maxFiles = getMaxUploadFiles( fieldId )
	return maxFiles == 0 ? true : filesCount < maxFiles; */
}

/**
 * Handles error
 * @param event event
 * @param ID id
 * @param fileObj  fileObj
 * @param data data
 * @param fieldId fieldId
 */
function handleError( event,ID,fileObj,data,fieldId ) {
	$('#' + fieldId).uploadifyCancel(ID);
	
	if ( data.type=="File Size" ) {
		var maxSize = data.info / 1024;
		var strMaxSize;
		
		if ( maxSize > 1024 )
		{
			maxSize = Math.round( maxSize / 1024 * 100 ) / 100;
			
			strMaxSize = maxSize + "Mo";
		}
		else
		{
			strMaxSize = Math.round( maxSize * 100 ) / 100 + "ko";
		}
		alert("Le fichier est trop gros. La taille est limitée à " + strMaxSize );
	}
	else
	{
		alert("Une erreur s'est produite lors de l'envoi du fichier : " + data.info );
	}
}

function directoryStartUpload( event, ID, fieldId )
{
	if( ! canUploadFile( fieldId ) )
	{
		$('#' + fieldId).uploadifyCancel(ID);
		return false;
	}
	uploading++;
	
	return true;
}

/**
 * Called when the upload if successfully completed
 * @param event event
 * @param ID id
 * @param fileObj fileObj
 * @param data data (json)
 */
function directoryOnUploadComplete(event,ID,fileObj,data)
{
	uploading--;
		
	var jsonData;
	try
	{
		jsonData = $.parseJSON(data);
	}
	catch ( err )
	{
		/* webapp conf problem : probably file upload limit */
		alert("Une erreur est survenue lors de l'envoi du fichier.");
		return;
	}
	
	
	if ( jsonData.directory_error != null )
	{
		alert( jsonData.directory_error );
	}
	
	directoryDisplayUploadedFiles( jsonData );
}

/**
 * Sets the files list
 * @param jsonData data
 */
function directoryDisplayUploadedFiles( jsonData )
{
	// create the div
	var fieldName = jsonData.field_name;
	
	if ( fieldName != null )
	{
		if ( jsonData.fileCount == 0 )
		{
			// no file uploaded, hiding content
			$("#_file_deletion_" + fieldName ).hide(  );
			$("#_file_deletion_label_" + fieldName ).hide(  );
			$("#_file_deletion_button_" + fieldName ).hide(  );
		}
		else
		{
			// show the hidden div (if not already)
			$("#_file_deletion_" + fieldName ).show(  );
			$("#_file_deletion_label_" + fieldName ).show(  );
			$("#_file_deletion_button_" + fieldName ).show(  );

			var strContent = "";
			var checkboxPrefix = '_directory_upload_checkbox_' + fieldName;
			
			// jsonData.uploadedFiles.length is str length when file count is 1 so using fileCount instead.
			// so if jsonData.fileCount == 1, the index should not be used
			for ( var index = 0; index < jsonData.fileCount; index++ ) {
					strContent = strContent + "<div class=\"form-element\"> \
						<span class=\"form-label\"><label>&nbsp;</label></span>  \
						<span class=\"form-field\" >  \
								<input type=\"checkbox\"  \
									name=\"" + checkboxPrefix + index + "\"  \
									id=\"" + checkboxPrefix + index + "\"  \
								/>  \
								&#160;" + ( (jsonData.fileCount == 1) ? jsonData.uploadedFiles : jsonData.uploadedFiles[index] ) +
								"&#160;(" + ( (jsonData.fileCount == 1) ? jsonData.uploadedFilesSize : jsonData.uploadedFilesSize[index] ) + " O)" +
						"</span>  \
					</div> ";
			}
			
			$("#_file_deletion_" + fieldName ).html(
					strContent
			);
		}
	}
}

// add asynchronous behaviour to inputs type=file
$('input[type=file]').each(function(index) {
	addAsynchronousUploadField(this.id);
});

// prevent user from quitting the page before his upload ended.
$('input[type=submit]').each(function() {
	$(this).click(function(event) {
		if ( uploading != 0 )
		{
			event.preventDefault();
			alert('Merci de patienter pendant l\'envoi du fichier');
		}
		else if ( this.name.match("_directory_upload_delete_"  ) )
		{
			event.preventDefault(); 
			removeFile(this.name);;
		}
	});
});

/**
 * Gets the max size value for the file
 * @param fieldId the file
 * @return the max size
 */
function getMaxLengthValue( fieldId ) {
	return getInputValue( '#_directory_upload_maxLength_' + fieldId );
}

/**
 * Get the file extensions
 * @param fieldId the id entry
 * @return the file extensions
 */
function getFileExtValue( fieldId ) {
	var value = getInputValue( '#_directory_upload_fileTypes_' + fieldId );
	if ( value != null ) {
		var reg = new RegExp( "[ ,;]+", "g" );
		var splitted = value.split(reg);
		value = "";
		var hashes = window.location.href.slice( window.location.href.indexOf( '?' ) + 1 ).split( '&' );
	    for( var i = 0; i < splitted.length; i++ ) {
	    	if ( i > 0 ) {
	    		value += ";"
	    	}
	    	value += "*." + splitted[i];
	    }
	}
	
	return value;
}

/**
 * Get the value of the input 
 * @param inputId the input id
 * @return the input value
 */
function getInputValue( inputId ) {
	var input = $( inputId )[0];
	if ( input != null ) {
		return input.value;
	}
	
	return null;
}

/**
 * Removes a file
 * @param form the form
 * @param subform the subform
 * @param action the action button name
 */
function removeFile( action ) {
	var fieldName = action.match("_directory_upload_delete_directory_(.*)")[1];
	
	// build indexes to remove
	var indexes = new Array();
	
	var indexesCount = 0;
	var checkboxPrefix = '_directory_upload_checkbox_directory_' + fieldName;
	$('[name^="' + checkboxPrefix + '"]:checked' ).each( function() {
		if (this.checked)
		{
			indexesCount++;
			var index = this.name.match( checkboxPrefix + "(\\d+)")[1] ;
			indexes.push(index);
		}
	});
	
	if ( !indexesCount )
	{
		return;
	}
	
	strIndexes = "[" + indexes + "]";
	var jsonData = {"id_entry":fieldName, "field_index": strIndexes};
	
	$.getJSON(baseUrl + 'jsp/site/plugins/directory/DoRemoveFile.jsp', jsonData,
		function(json) {
			directoryDisplayUploadedFiles(json);
		}
	);
}

function keepAlive(  ) {
	if ( uploading > 0 )
	{
		$.getJSON(baseUrl + 'jsp/admin/plugins/directory/KeepAlive.jsp');
	}
	setTimeout("keepAlive()", 240000);
}

keepAlive(  );

/**
 * Init the plugin liveupdate postit
 * @param url the admin base url with context
 * @param fileName the file name
 */
function initUploadify( baseUrl ) {
	var link = '<link rel="stylesheet" href="';
	link = link + baseUrl;
	link = link + '/css/plugins/directory/uploadify/uploadify.css" type="text/css" media="all" />';
	$( 'head' ).append( link );
}

//Init uploadify
initUploadify( baseUrl );
