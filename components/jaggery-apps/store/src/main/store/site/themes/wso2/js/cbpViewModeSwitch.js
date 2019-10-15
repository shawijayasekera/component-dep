/**
 * cbpViewModeSwitch.js v1.0.0
 * http://www.codrops.com
 *
 * Licensed under the MIT license.
 * http://www.opensource.org/licenses/mit-license.php
 * 
 * Copyright 2013, Codrops
 * http://www.codrops.com
 */
(function() {

	var container = document.getElementById( 'cbp-vm' );

	if(container !== null) { //to Enable Switch Mode only if the widget presents - API page only

		var optionSwitch = Array.prototype.slice.call(container.querySelectorAll('div.cbp-vm-options > a'));

		var containerRecentVal = document.getElementById('cbp-vm-recent');

		if (containerRecentVal !== null) {
			var containerRecent = document.getElementById('cbp-vm-recent'),
				optionSwitchRecent = Array.prototype.slice.call(containerRecent.querySelectorAll('div.cbp-vm-options > a'));
		}

		function init() {
			optionSwitch.forEach(function (el, i) {
				el.addEventListener('click', function (ev) {
					ev.preventDefault();
					_switch(this);
				}, false);
			});

			if (containerRecentVal !== null) {
				optionSwitchRecent.forEach(function (el, i) {
					el.addEventListener('click', function (ev) {
						ev.preventDefault();
						_switchRecent(this);
					}, false);
				});
			}
		}

		function _switch(opt) {
			// remove other view classes and any any selected option
			optionSwitch.forEach(function (el) {
				classie.remove(container, el.getAttribute('data-view'));
				classie.remove(el, 'cbp-vm-selected');
			});
			// add the view class for this option
			classie.add(container, opt.getAttribute('data-view'));
			// this option stays selected
			classie.add(opt, 'cbp-vm-selected');
		}

		function _switchRecent(opt) {
			// remove other view classes and any any selected option
			optionSwitchRecent.forEach(function (el) {
				classie.remove(containerRecent, el.getAttribute('data-view'));
				classie.remove(el, 'cbp-vm-selected');
			});
			// add the view class for this option
			classie.add(containerRecent, opt.getAttribute('data-view'));
			// this option stays selected
			classie.add(opt, 'cbp-vm-selected');
		}

		init();

	}
})();