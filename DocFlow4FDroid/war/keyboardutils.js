var keyboardchange;
function setkeyboardPress(elem) {
	elem.onkeypress = function(e) {
		if (e.altKey || e.ctrlKey)
			return true;
		var f = e.target;

		var s = f.getAttribute("changes");
		if (!s)
			return true;
		var codes = s.split(';');

		var replacement = [];
		for (i = 0; i < codes.length; i++) {
			var unit = codes[i].split(':');
			if (unit.length && unit.length == 2) {
				replacement[unit[0]] = unit[1];
			}
		}
		var key = window.event ? e.keyCode : e.which;
		var ckey = replacement[key];
		if (!ckey)
			return true;
		if (ckey == key)
			return true;

		var g = f.selectionStart; // აქ ვიღევთ რამდენი სიმბოლო არის აკრეფილი
		// კურსორამდე
		f.value = f.value.substring(0, f.selectionStart) // კურსორამდე მთელი
				// ტექსტი
				+ String.fromCharCode(ckey) // დამატებული ახალი აკრეფილი სიმბოლო
				// (უკვე გადაყვანილი)
				+ f.value.substring(f.selectionEnd); // დამატებული კურსორის
		// მერე რაც წერია

		// f.value+=String.fromCharCode(geo[i]);
		g++;
		f.setSelectionRange(g, g);
		return false;

	}
}