/**
 * The global page refresh interval for auto-refresh pages (sec).
 */
var pageRefreshInterval = 30;

/**
 * Temporary refresh countdown counter. 
 */
var currentRefreshCounter = -1;

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

/**
 * Performs a count-down and auto-refreshes the page, after the interval specified 
 * in variable 'pageRefreshInterval'. Updates the innerHTML of the element with id
 * 'pageRefreshTimer', if it exists.
 * Must be called once to begin count-down. 
 */
function autoRefreshPage() {
	if (currentRefreshCounter == -1) {
		currentRefreshCounter = pageRefreshInterval;
	}
	
	if (currentRefreshCounter == 1) {
		window.location.reload();
	} else {
		currentRefreshCounter -= 1;
		var min = Math.floor(currentRefreshCounter / 60);
		var sec = currentRefreshCounter % 60;
		var timerStatus = document.getElementById('pageRefreshTimer');
		if (timerStatus != null) {
			timerStatus.innerHTML = min + ':' + (sec >= 10 ? sec : '0' + sec);
		}
		setTimeout('autoRefreshPage()', 1000);
	}
}

/**
 * Perfrom a form action.
 * Set actionValue as the value of the hidden with actionId and submit the 
 * form with id formId.
 */ 
function doFormAction(actionId, actionValue, formId) {
	var hiddenEl = document.getElementById(actionId);
	var formEl = document.getElementById(formId);
	hiddenEl.value = actionValue;
	formEl.submit();
	return false;
}