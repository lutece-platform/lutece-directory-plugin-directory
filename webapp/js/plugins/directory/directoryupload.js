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
	    
	    $( '#update_entry_' + fieldId ).hide(  );
	    if ( $( '#delete_' + fieldId ) )
	    {
	    	var fileName = $( '#_filename_' + fieldId + ' input[type="hidden"]' ).val(  );
	    	if ( fileName )
	    	{
	    		var anchorId = '_img_remove_file_' + fieldId;
	    		$( '#_filename_' + fieldId).append( getImageRemoveFile( anchorId, fieldId ) );
				$( '#' + anchorId).click( 
						function( event ) {
							if ( confirm( 'Voulez-vous vraiment supprimer le fichier ?' ) ) {
								var jsonData = { 'id_entry' : fieldId };
								$.getJSON( baseUrl + 'jsp/admin/plugins/directory/DoRemoveFile.jsp', jsonData,
									function( json ) {
									$( '#_filename_' + fieldId).hide(  );
								} );
								event.preventDefault();
								$( '#_filename_' + fieldId ).html( getUpdateEntryHiddenInput( fieldId ) );
							} else {
								return false;
							}
						}
				);
	    	}
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
	
	
	if ( jsonData.error != null )
	{
		alert( jsonData.error );
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
		displayFile( jsonData.files[0].fileName, fieldName );
	}
}

// add asynchronous behaviour to inputs type=file
$('input[type=file].asynchronouse_upload').each(function(index) {
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
	});
});

function displayFile( fileName, fieldId )
{
	var anchorId = '_img_remove_file_' + fieldId;
	var strContent =  fileName + "&nbsp;" + getImageRemoveFile( anchorId, fieldId ) + getUpdateEntryHiddenInput( fieldId );
	$("#_filename_" + fieldId).html( strContent );
	$("#" + anchorId).click( 
			function( event ) {
				if ( confirm( 'Voulez-vous vraiment supprimer le fichier ?' ) ) {
					var jsonData = { "id_entry" : fieldId };
					$.getJSON(baseUrl + 'jsp/admin/plugins/directory/DoRemoveFile.jsp', jsonData,
							function(json) {
						$( '#_filename_' + fieldId).hide(  );
					}
					);				
					event.preventDefault();
					$( '#_filename_' + fieldId).html( '' );
				} else {
					return false;
				}
			}
	);
}

function getImageRemoveFile( anchorId, fieldId )
{
	return '<a href="#" id="'+ anchorId + '"><img src="images/local/skin/plugins/directory/cancel.png" title="Supprimer" alt="Supprimer" /></a>';
}

function getUpdateEntryHiddenInput( fieldId )
{
	return '<input type="hidden" name="update_entry_' + fieldId + '" value="true" />';
}

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
