/**
 * Prompts the user before submitting the form with name 'form'.
 * If a field name and value are defined they are updated before submission.
 */
function confirmDelete(message, fieldName, fieldValue) {
	var result = window.confirm(message);
	if (result) {
		if (fieldName != null) {
			document.forms['form'].elements[fieldName].value = fieldValue;
		}
		document.forms['form'].submit();
	}
	return result;
}

/**
 * Opens the URL in a new window with a given name. 
 */
function openWindow(url) {
	window.open(
		url, 
		'graph', 
		'resizable=true,toolbar=false,status=false,location=false,menubar=false');
	return true;
}