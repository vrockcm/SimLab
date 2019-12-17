
/* The dragging code for '.draggable' from the demo above
 * applies to this demo as well so it doesn't have to be repeated. */
;(function(window) {

	'use strict';

	var support = { animations : Modernizr.cssanimations },
		animEndEventNames = { 'WebkitAnimation' : 'webkitAnimationEnd', 'OAnimation' : 'oAnimationEnd', 'msAnimation' : 'MSAnimationEnd', 'animation' : 'animationend' },
		animEndEventName = animEndEventNames[ Modernizr.prefixed( 'animation' ) ],
		onEndAnimation = function( el, callback ) {
			var onEndCallbackFn = function( ev ) {
				if( support.animations ) {
					if( ev.target != this ) return;
					this.removeEventListener( animEndEventName, onEndCallbackFn );
				}
				if( callback && typeof callback === 'function' ) { callback.call(); }
			};
			if( support.animations ) {
				el.addEventListener( animEndEventName, onEndCallbackFn );
			}
			else {
				onEndCallbackFn();
			}
		};

	function extend( a, b ) {
		for( var key in b ) {
			if( b.hasOwnProperty( key ) ) {
				a[key] = b[key];
			}
		}
		return a;
	}

	function MLMenu(el, options) {
		this.el = el;
		this.options = extend( {}, this.options );
		extend( this.options, options );

		// the menus (<ul>´s)
		this.menus = [].slice.call(this.el.querySelectorAll('.menu__level'));

		// index of current menu
		// Each level is actually a different menu so 0 is root, 1 is sub-1, 2 sub-2, etc.
		this.current_menu = 0;

		/* Determine what current menu actually is */
		var current_menu;
		this.menus.forEach(function(menuEl, pos) {
			var items = menuEl.querySelectorAll('.menu__item');
			items.forEach(function(itemEl, iPos) {
				var currentLink = itemEl.querySelector('.menu__link--current');
				if (currentLink) {
					// This is the actual menu__level that should have current
					current_menu = pos;
				}
			});
		});

		if (current_menu) {
			this.current_menu = current_menu;
		}

		this._init();
	}

	MLMenu.prototype.options = {
		// show breadcrumbs
		breadcrumbsCtrl : true,
		// initial breadcrumb text
		initialBreadcrumb : 'all',
		// show back button
		backCtrl : true,
		// delay between each menu item sliding animation
		itemsDelayInterval : 60,
		// direction
		direction : 'r2l',
		// callback: item that doesn´t have a submenu gets clicked
		// onItemClick([event], [inner HTML of the clicked item])
		onItemClick : function(ev, itemName) { return false; }
	};

	MLMenu.prototype._init = function() {
		// iterate the existing menus and create an array of menus,
		// more specifically an array of objects where each one holds the info of each menu element and its menu items
		this.menusArr = [];
		this.breadCrumbs = false;
		var self = this;
		var submenus = [];

		/* Loops over root level menu items */
		this.menus.forEach(function(menuEl, pos) {
			var menu = {menuEl : menuEl, menuItems : [].slice.call(menuEl.querySelectorAll('.menu__item'))};

			self.menusArr.push(menu);

			// set current menu class
			if( pos === self.current_menu ) {
				classie.add(menuEl, 'menu__level--current');
			}

			var menu_x = menuEl.getAttribute('data-menu');
			var links = menuEl.querySelectorAll('.menu__link');
			links.forEach(function(linkEl, lPos) {
				var submenu = linkEl.getAttribute('data-submenu');
				if (submenu) {
					var pushMe = {"menu":submenu, "name": linkEl.innerHTML };
					if (submenus[pos]) {
						submenus[pos].push(pushMe);
					} else {
						submenus[pos] = []
						submenus[pos].push(pushMe);
					}
				}
			});
		});

		/* For each MENU, find their parent MENU */
		this.menus.forEach(function(menuEl, pos) {
			var menu_x = menuEl.getAttribute('data-menu');
			submenus.forEach(function(subMenuEl, menu_root) {
				subMenuEl.forEach(function(subMenuItem, subPos) {
					if (subMenuItem.menu == menu_x) {
						self.menusArr[pos].backIdx = menu_root;
						self.menusArr[pos].name = subMenuItem.name;
					}
				});
			});
		});

		// create breadcrumbs
		if( self.options.breadcrumbsCtrl ) {
			this.breadcrumbsCtrl = document.createElement('nav');
			this.breadcrumbsCtrl.className = 'menu__breadcrumbs';
			this.breadcrumbsCtrl.setAttribute('aria-label', 'You are here');
			this.el.insertBefore(this.breadcrumbsCtrl, this.el.firstChild);
			// add initial breadcrumb
			this._addBreadcrumb(0);

			// Need to add breadcrumbs for all parents of current submenu
			if (self.menusArr[self.current_menu].backIdx != 0 && self.current_menu != 0) {
				this._crawlCrumbs(self.menusArr[self.current_menu].backIdx, self.menusArr);
				this.breadCrumbs = true;
			}

			// Create current submenu breadcrumb
			if (self.current_menu != 0) {
				this._addBreadcrumb(self.current_menu);
				this.breadCrumbs = true;
			}
		}

		// create back button
		if (this.options.backCtrl) {
			this.backCtrl = document.createElement('button');
			if (this.breadCrumbs) {
				this.backCtrl.className = 'menu__back';
			} else {
				this.backCtrl.className = 'menu__back menu__back--hidden';
			}
			this.backCtrl.setAttribute('aria-label', 'Go back');
			this.backCtrl.innerHTML = '<span class="icon icon--arrow-left"></span>';
			this.el.insertBefore(this.backCtrl, this.el.firstChild);
		}

		// event binding
		this._initEvents();
	};

	MLMenu.prototype._initEvents = function() {
		var self = this;

		for(var i = 0, len = this.menusArr.length; i < len; ++i) {
			this.menusArr[i].menuItems.forEach(function(item, pos) {
				item.querySelector('a').addEventListener('click', function(ev) {
					var submenu = ev.target.getAttribute('data-submenu'),
						itemName = ev.target.innerHTML,
						subMenuEl = self.el.querySelector('ul[data-menu="' + submenu + '"]');

					// check if there's a sub menu for this item
					if( submenu && subMenuEl ) {
						ev.preventDefault();
						// open it
						self._openSubMenu(subMenuEl, pos, itemName);
					}
					else {
						// add class current
						var currentlink = self.el.querySelector('.menu__link--current');
						if( currentlink ) {
							classie.remove(self.el.querySelector('.menu__link--current'), 'menu__link--current');
						}
						classie.add(ev.target, 'menu__link--current');

						// callback
						self.options.onItemClick(ev, itemName);
					}
				});
			});
		}

		// back navigation
		if( this.options.backCtrl ) {
			this.backCtrl.addEventListener('click', function() {
				self._back();
			});
		}
	};

	MLMenu.prototype._openSubMenu = function(subMenuEl, clickPosition, subMenuName) {
		if( this.isAnimating ) {
			return false;
		}
		this.isAnimating = true;

		// save "parent" menu index for back navigation
		this.menusArr[this.menus.indexOf(subMenuEl)].backIdx = this.current_menu;
		// save "parent" menu´s name
		this.menusArr[this.menus.indexOf(subMenuEl)].name = subMenuName;
		// current menu slides out
		this._menuOut(clickPosition);
		// next menu (submenu) slides in
		this._menuIn(subMenuEl, clickPosition);
	};

	MLMenu.prototype._back = function() {
		if( this.isAnimating ) {
			return false;
		}
		this.isAnimating = true;

		// current menu slides out
		this._menuOut();
		// next menu (previous menu) slides in
		var backMenu = this.menusArr[this.menusArr[this.current_menu].backIdx].menuEl;
		this._menuIn(backMenu);

		// remove last breadcrumb
		if( this.options.breadcrumbsCtrl ) {
			this.breadcrumbsCtrl.removeChild(this.breadcrumbsCtrl.lastElementChild);
		}
	};

	MLMenu.prototype._menuOut = function(clickPosition) {
		// the current menu
		var self = this,
			currentMenu = this.menusArr[this.current_menu].menuEl,
			isBackNavigation = typeof clickPosition == 'undefined' ? true : false;

		// slide out current menu items - first, set the delays for the items
		this.menusArr[this.current_menu].menuItems.forEach(function(item, pos) {
			item.style.WebkitAnimationDelay = item.style.animationDelay = isBackNavigation ? parseInt(pos * self.options.itemsDelayInterval) + 'ms' : parseInt(Math.abs(clickPosition - pos) * self.options.itemsDelayInterval) + 'ms';
		});
		// animation class
		if( this.options.direction === 'r2l' ) {
			classie.add(currentMenu, !isBackNavigation ? 'animate-outToLeft' : 'animate-outToRight');
		}
		else {
			classie.add(currentMenu, isBackNavigation ? 'animate-outToLeft' : 'animate-outToRight');
		}
	};

	MLMenu.prototype._menuIn = function(nextMenuEl, clickPosition) {
		var self = this,
			// the current menu
			currentMenu = this.menusArr[this.current_menu].menuEl,
			isBackNavigation = typeof clickPosition == 'undefined' ? true : false,
			// index of the nextMenuEl
			nextMenuIdx = this.menus.indexOf(nextMenuEl),

			nextMenu = this.menusArr[nextMenuIdx],
			nextMenuEl = nextMenu.menuEl,
			nextMenuItems = nextMenu.menuItems,
			nextMenuItemsTotal = nextMenuItems.length;

		// slide in next menu items - first, set the delays for the items
		nextMenuItems.forEach(function(item, pos) {
			item.style.WebkitAnimationDelay = item.style.animationDelay = isBackNavigation ? parseInt(pos * self.options.itemsDelayInterval) + 'ms' : parseInt(Math.abs(clickPosition - pos) * self.options.itemsDelayInterval) + 'ms';

			// we need to reset the classes once the last item animates in
			// the "last item" is the farthest from the clicked item
			// let's calculate the index of the farthest item
			var farthestIdx = clickPosition <= nextMenuItemsTotal/2 || isBackNavigation ? nextMenuItemsTotal - 1 : 0;

			if( pos === farthestIdx ) {
				onEndAnimation(item, function() {
					// reset classes
					if( self.options.direction === 'r2l' ) {
						classie.remove(currentMenu, !isBackNavigation ? 'animate-outToLeft' : 'animate-outToRight');
						classie.remove(nextMenuEl, !isBackNavigation ? 'animate-inFromRight' : 'animate-inFromLeft');
					}
					else {
						classie.remove(currentMenu, isBackNavigation ? 'animate-outToLeft' : 'animate-outToRight');
						classie.remove(nextMenuEl, isBackNavigation ? 'animate-inFromRight' : 'animate-inFromLeft');
					}
					classie.remove(currentMenu, 'menu__level--current');
					classie.add(nextMenuEl, 'menu__level--current');

					//reset current
					self.current_menu = nextMenuIdx;

					// control back button and breadcrumbs navigation elements
					if( !isBackNavigation ) {
						// show back button
						if( self.options.backCtrl ) {
							classie.remove(self.backCtrl, 'menu__back--hidden');
						}

						// add breadcrumb
						self._addBreadcrumb(nextMenuIdx);
					}
					else if( self.current_menu === 0 && self.options.backCtrl ) {
						// hide back button
						classie.add(self.backCtrl, 'menu__back--hidden');
					}

					// we can navigate again..
					self.isAnimating = false;

					// focus retention
					nextMenuEl.focus();
				});
			}
		});

		// animation class
		if( this.options.direction === 'r2l' ) {
			classie.add(nextMenuEl, !isBackNavigation ? 'animate-inFromRight' : 'animate-inFromLeft');
		}
		else {
			classie.add(nextMenuEl, isBackNavigation ? 'animate-inFromRight' : 'animate-inFromLeft');
		}
	};

	MLMenu.prototype._addBreadcrumb = function(idx) {
		if( !this.options.breadcrumbsCtrl ) {
			return false;
		}

		var bc = document.createElement('a');
		bc.href = '#'; // make it focusable
		bc.innerHTML = idx ? this.menusArr[idx].name : this.options.initialBreadcrumb;
		this.breadcrumbsCtrl.appendChild(bc);

		var self = this;
		bc.addEventListener('click', function(ev) {
			ev.preventDefault();

			// do nothing if this breadcrumb is the last one in the list of breadcrumbs
			if( !bc.nextSibling || self.isAnimating ) {
				return false;
			}
			self.isAnimating = true;

			// current menu slides out
			self._menuOut();
			// next menu slides in
			var nextMenu = self.menusArr[idx].menuEl;
			self._menuIn(nextMenu);

			// remove breadcrumbs that are ahead
			var siblingNode;
			while (siblingNode = bc.nextSibling) {
				self.breadcrumbsCtrl.removeChild(siblingNode);
			}
		});
	};

	MLMenu.prototype._crawlCrumbs = function(currentMenu, menuArray) {
		if (menuArray[currentMenu].backIdx != 0) {
			this._crawlCrumbs(menuArray[currentMenu].backIdx, menuArray);
		}
		// create breadcrumb
		this._addBreadcrumb(currentMenu);
	}

	window.MLMenu = MLMenu;

})(window);

$(document).ready(function() {
    // Set the date we're counting down to
    var countDownDate = new Date();
    countDownDate.setMinutes(countDownDate.getMinutes()+timeLimit);
    // Update the count down every 1 second
    if(timeLimit != 0){
        var x = setInterval(function() {
                var now = new Date().getTime();
                var distance = countDownDate - now;

                var days = Math.floor(distance / (1000 * 60 * 60 * 24));
                var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                var seconds = Math.floor((distance % (1000 * 60)) / 1000);

                $(".timer-text").text("Time Remaining: "+hours + "h "
                + minutes + "m " + seconds + "s ");

                // If the count down is finished, write some text
                if (distance < 0) {
                    clearInterval(x);
                    finishLab();
                }
        }, 1000);
    }
    else
        $(".timer-text").text("Time Remaining: No Limit");

    $(".steps option:first").removeAttr("disabled");
    $(".steps").val($(".steps option:first").val());
    $('.steps').formSelect();
    //This gets the email from the front end and passes calls the loadCourses function with this email.
    initialize();

        function initialize() {
        		var menuEl = document.getElementById('ml-menu'),
        			mlmenu = new MLMenu(menuEl, {
        				// breadcrumbsCtrl : true, // show breadcrumbs
        				// initialBreadcrumb : 'all', // initial breadcrumb text
        				breadcrumbsCtrl : false,
        				backCtrl : false, // show back button
        				// itemsDelayInterval : 60, // delay between each menu item sliding animation
        			});

        		// mobile menu toggle
        		var openMenuCtrl = document.querySelector('.action--open'),
        			closeMenuCtrl = document.querySelector('.action--close');

        		openMenuCtrl.addEventListener('click', openMenu);
        		closeMenuCtrl.addEventListener('click', closeMenu);

        		function openMenu() {
        			classie.add(menuEl, 'menu--open');
        			closeMenuCtrl.focus();
        		}

        		function closeMenu() {
        			classie.remove(menuEl, 'menu--open');
        			openMenuCtrl.focus();
        		}
        }
});
// enable draggables to be dropped into this

function openNav(x) {
  document.getElementById("mySidenav").style.right = "0px";
  $("#SolutionName").text($(x).data("key").name);
  $("#SolutionTemp").text($(x).data("key").cumTemp+" °C");
  $("#SolutionCap").text($(x).data("key").capacity+"mL");
  $("#SolutionVol").text($(x).data("key").cumVolume+"mL");
  $("#SolutionpH").text($(x).data("key").cumPH);
  $("#SolutionSwirl").text($(x).data("key").swirled);
  $("#SolutionsList").empty();
  for (b of $(x).data("key").solutions)
    $("#SolutionsList").append("<tr><td>"+b.solutionName+"</td></tr>")
}

function closeNav() {
  document.getElementById("mySidenav").style.right = "-250px";
}

interact('.workbench').dropzone({
  // only accept elements matching this CSS selector
  // Require a 75% element overlap for a drop to be possible
  overlap: 0.50,
  ondragenter: function (event) {
    var draggableElement = event.relatedTarget
    var dropzoneElement = event.target

    // feedback the possibility of a drop
    if ($(draggableElement).hasClass("card")){
        draggableElement.classList.add('green')
    }
  },
  ondragleave: function (event) {
    // remove the drop feedback style
    if ($(event.relatedTarget).hasClass("card")){
        event.relatedTarget.classList.remove('green')
    }
  }
}).on('tap', function (event) {
    if($(event.target).hasClass("workbench")){
          closeNav();
          $(".drag-material").popover('dispose');
          $(".drag-material").removeClass('dashed-outline');
          $(".drag-material").find(".view").removeClass('pour dip BurnerOn');
          $($(".drag-material").find(".mat-name")).removeClass("top top-right");
    }
});

function dragMoveListener (event) {
    var target = event.target;
    var x = (parseFloat(target.getAttribute('data-x')) || 0) + event.dx;
    var y = (parseFloat(target.getAttribute('data-y')) || 0) + event.dy;

    target.style.webkitTransform =
    target.style.transform = 'translate(' + x + 'px, ' + y + 'px)';

    target.setAttribute('data-x', x);
    target.setAttribute('data-y', y);
}

interact('.drag-material').draggable({
    modifiers: [
        interact.modifiers.restrictRect({
            restriction: 'parent',
            endOnly: true
        })
    ],
    inertia: true,
    onstart: function (event) {
        $(".drag-material").popover('dispose');
        $(".drag-material").removeClass('dashed-outline');
        $(event.target).addClass('dashed-outline');
        $(".drag-material").find(".view").removeClass('pour dip BurnerOn');
        $($(".drag-material").find(".mat-name")).removeClass("top top-right");
    },
    onmove: dragMoveListener,
    onend: function(event) {
        var dropzoneElement = event.target;
    },
})

$.fn.hasAnyClass = function() {
    for (var i = 0; i < arguments.length; i++) {
        var classes = arguments[i].split(" ");
        for (var j = 0; j < classes.length; j++) {
            if (this.hasClass(classes[j])) {
                return true;
            }
        }
    }
    return false;
}

interact('.drag-material').dropzone({
  // only accept elements matching this CSS selector
  // Require a 75% element overlap for a drop to be possible
  overlap: 0.50,
  ondrop:function (event) {
     var draggableElement = event.relatedTarget
     var dropzoneElement = event.target
     closeNav();
     $(".drag-material").removeClass('dashed-outline');

     if($(draggableElement).find(".view").hasAnyClass("Pipette") && !$(dropzoneElement).find(".view").hasAnyClass("Bunsen","Scale", "Pipette")){

         $(draggableElement).find(".view").addClass('dip');
         $($(draggableElement).find(".mat-name")).addClass("top");
         $(draggableElement).offset({ top: ($(dropzoneElement).offset().top - $(dropzoneElement).height()/2) , left: ($(dropzoneElement).offset().left + $(dropzoneElement).width()/2- 100)});

         $(draggableElement).popover({
         container: 'body',
         html: true,
         placement: 'right',
         sanitize: false,
         content:
         `<div id="PopoverContent">
           <div class="input-group">
                <div class="row">
                    <div class="col">
                        <div class="scroll-zone1"></div>
                    </div>
                    <div class="col">
                        <div class="scroll-zone2"></div>
                    </div>
                </div>
                <div class="row">
                     <div class="col">
                        <h6>Precision Level (0.50)</h6>
                        <h6 class="quant">0mL</h6>
                     </div>
                     <div class="col">
                        <h6>Precision Level (0.01)</h6>
                        <h6 class="quant">0mL</h6>
                     </div>
                </div>
                <div class="row">
                     <div class="col">
                        <a id="submit-draw" class="waves-effect waves-light btn-small">Draw Up</a>
                        <a id="submit-release" class="waves-effect waves-light btn-small">Release</a>
                     </div>
                </div>
           </div>
         </div>`
         });
         $(draggableElement).popover('show');

         var number = $(draggableElement).data("key").cumVolume;
         var currentpos = 140 - quan1*7.2;
         $(".quant").text(number+"mL");
         $(".scroll-zone1, .scroll-zone2").css("background-position","center, 0px "+currentpos+"px");

       currentpos = 140;
       $('.scroll-zone1').bind('mousewheel', function(e){
           if(e.originalEvent.wheelDelta /120 > 0) {
               if(number<30){
                    number+=0.5;
                    currentpos -= 3.6;
               }
           }
           else{
               if(number>0){
                   number-= 0.5;
                   currentpos += 3.6;
               }
           }
           $(".quant").text(number.toFixed(2)+"mL");
           $(".scroll-zone1, .scroll-zone2").css("background-position","center, 0px "+currentpos+"px");
       });
       $('.scroll-zone2').bind('mousewheel', function(e){
          if(e.originalEvent.wheelDelta /120 > 0) {
              if(number<30){
                   number+=0.01;
                   currentpos -= 0.072;
              }
          }
          else{
              if(number>0){
                  number-= 0.01;
                  currentpos += 0.072;
              }
          }
          $(".quant").text(number.toFixed(2)+"mL");
          $(".scroll-zone1, .scroll-zone2").css("background-position","center, 0px "+currentpos+"px");
      });

       $('#submit-draw').click(function(){
            drawUp(dropzoneElement, draggableElement, number.toFixed(2));
          $(draggableElement).popover('dispose');
       });
        $('#submit-release').click(function(){
           release(draggableElement, dropzoneElement, number.toFixed(2));
           $(draggableElement).popover('dispose');
        });

     }
     else if($(dropzoneElement).find(".view").hasAnyClass("Bunsen") && !$(draggableElement).find(".view").hasAnyClass("Bunsen", "Scale","Pipette")){
          $(dropzoneElement).find(".view").addClass("BurnerOn");
          $(draggableElement).offset({ top: ($(dropzoneElement).offset().top - $(dropzoneElement).height()/2) , left: ($(dropzoneElement).offset().left + $(dropzoneElement).width()/2 - 104)});
          $($(draggableElement).find(".mat-name")).addClass("top-right");

     }else if(!$(dropzoneElement).find(".view").hasAnyClass("Bunsen","Scale","Pipette") && !$(draggableElement).find(".view").hasAnyClass("Bunsen","Scale","Pipette")){
         $(draggableElement).find(".view").addClass('pour');
         $(draggableElement).offset({ top: ($(dropzoneElement).offset().top - $(dropzoneElement).height()/2) , left: ($(dropzoneElement).offset().left + $(dropzoneElement).width()/2 - 30)});
         $($(draggableElement).find(".mat-name")).addClass("top-right");
         $(dropzoneElement).popover({
         container: 'body',
         html: true,
         placement: 'bottom',
         sanitize: false,
         content:
         `<div id="PopoverContent">
           <div class="input-group">
                <div class="w-100" style="clear: both">
                    <h6 style="float: left">Quantity</h6>
                    <h6 class="quant" style="float: right">0mL</h6>
                </div>
              <div class="progress">
                <div class="determinate"></div>
              </div>
             <a class="waves-effect waves-light btn hold-pour">Hold to Pour</a>
             </div>
           </div>
         </div>`
         });
         $(dropzoneElement).popover('show');
         $(".determinate").width('0%');
         var number = 0;
         var timeout,interval = 0;


         $('.hold-pour').on('mousedown', function() {
            menu_toggle(draggableElement,dropzoneElement);
            timeout = setTimeout(function() {
               interval = setInterval(function() {
                 console.log("called");
                 menu_toggle();
               }, 200);
             }, 300);
         }).on('mouseup', function() {
             clearTimers();
         }).on('mouseleave', function() {
            clearTimeout(timeout);
            clearInterval(interval);
            number = 0;
            timeout,interval = 0;
            $(".determinate").width('0%');
            $(".quant").text("0mL");
        })

         function clearTimers() {
             clearTimeout(timeout);
             clearInterval(interval);
             pour(draggableElement, dropzoneElement,number);
             number = 0;
             timeout,interval = 0;
             $(".determinate").width('0%');
             $(".quant").text("0mL");
         }
         var quan1  = $(draggableElement).data("key").cumVolume;
         var quan2 = $(dropzoneElement).data("key").cumVolume;
         var quan2cap = $(dropzoneElement).data("key").capacity;

         function menu_toggle() {
            var x = Math.random() * 3;
            console.log("x:"+x);
            if((quan1-number)<=3)
                x = (quan1-number);
            if((quan2cap - (quan2+number))<=3)
                x = (quan2cap - (quan2+number));

            if((number+x)<=quan1 && (quan2+number+x)<=quan2cap){
                console.log("Went in");
                number += x;
                console.log("number:"+number);
                var f = $(".determinate").width() / $('.determinate').parent().width() * 100;
                $(".determinate").width((f+x)+"%");
                $(".quant").text(number.toFixed(2)+"mL");
            }
         }
     }else if($(dropzoneElement).find(".view").hasAnyClass("Scale") && !$(draggableElement).find(".view").hasAnyClass("Bunsen","Scale","Pipette")){
              $(draggableElement).offset({ top: ($(dropzoneElement).offset().top - $(dropzoneElement).height()/2)+25 , left: ($(dropzoneElement).offset().left + $(dropzoneElement).width()/2 -105)});
              $(draggableElement).insertAfter($(dropzoneElement));
              $($(draggableElement).find(".mat-name")).addClass("top-right");

      }
  }
}).on('tap', function (event) {
      $(".drag-material").removeClass('dashed-outline');
      $(event.currentTarget).addClass('dashed-outline');
      event.stopImmediatePropagation();
}).on('doubletap', function (event) {
      openNav(event.currentTarget);
      var dropzoneElement = event.currentTarget;
      $(dropzoneElement).popover({
       container: 'body',
       html: true,
       placement: 'bottom',
       sanitize: false,
       content:
       `<div id="PopoverContent">
         <div class="input-group">
           <a class="waves-effect waves-light btn Swirl-Button">Swirl</a>
         </div>
       </div>`
       });
       $(dropzoneElement).popover('show');
       $(".Swirl-Button").click(function(){
           $($(dropzoneElement).find(".view")).addClass("shake animated").one('animationend webkitAnimationEnd oAnimationEnd', function() {
               $($(dropzoneElement).find(".view")).removeClass("shake animated");
           });
          swirl(dropzoneElement);
       });
});

interact('.trash').dropzone({
  // only accept elements matching this CSS selector
  // Require a 75% element overlap for a drop to be possible
  overlap: 0.25,
  accept: '.drag-material',
  ondragenter: function (event) {
      var draggableElement = event.relatedTarget
      var dropzoneElement = event.target
      $($(dropzoneElement).find(".trash-can")).addClass('pop-up');
  },
  ondragleave: function (event) {
      var draggableElement = event.relatedTarget
      var dropzoneElement = event.target
      $($(dropzoneElement).find(".trash-can")).removeClass('pop-up');
  },
  ondrop: function (event) {
     var draggableElement = event.relatedTarget
     var dropzoneElement = event.target
     interact(dropzoneElement).unset();
     var matName = $($(dropzoneElement).find(".drag-material")).find(".mat-name").text();
     var draggableElement = event.relatedTarget
     var dropzoneElement = event.target
     $($(dropzoneElement).find(".trash-can")).removeClass('pop-up');
     $(draggableElement).hide('fast', function(){ $(draggableElement).remove(); });

     removeFromWorkbench($(draggableElement).data("key").name);
  }
})




interact.dynamicDrop(true);
interact('.card').draggable({
    modifiers: [
        interact.modifiers.restrict({
            restrictRect: 'parent',
            endOnly: true
        })
    ],
    inertia: true,
    onmove: dragMoveListener,
    onend: function(event) {
        var dropzoneElement = event.target;
        if (!($(event.relatedTarget).hasClass("workbench"))){
            $(dropzoneElement).hide('fast', function(){ $(dropzoneElement).remove(); });
        }
        else{
            var mat = $(dropzoneElement).find(".parentDiv");
            $(mat).addClass("drag-material");
            var matName = $(mat).find(".mat-name").text();
            $(mat).css("pointer-events","auto");
            $(mat).css("position","absolute");
            $($(dropzoneElement).find(".mat-name")).show();
            $(event.relatedTarget).append(mat);
            $(mat).offset({ top: $(dropzoneElement).offset().top, left:  $(dropzoneElement).offset().left});
            $(mat).width(200);
            $(mat).height(250);
            interact(dropzoneElement).unset();
            $(dropzoneElement).remove();
            if($(mat).attr("value") == "tool"){
                moveToolToWorkBench(mat,matName);
            }
            else{
                moveToWorkBench(mat,matName);
            }
        }
    }
}).on('move', function (event) {
var interaction = event.interaction;
if (interaction.pointerIsDown && !interaction.interacting() && event.currentTarget.getAttribute('clonable') != 'false') {
  var original = event.currentTarget;
  var clone = original.cloneNode(true);
  clone.setAttribute('clonable','false');
  clone.style.position = "absolute";
  $(clone).offset({ top: $(original).offset().top, left:  $(original).offset().left});
  $(clone).width($(original).width());
  $(clone).height($(original).height());
  document.body.appendChild(clone);
  interaction.start({ name: 'drag' },event.interactable,clone);
}
});

//Ajax functions for different user interactions

//Moving material into workbench
function moveToWorkBench(ele,materialName){
   $.ajax({
        url : '/moveToWorkBench',
        type : 'POST',
        data : {
            'materialName' : materialName
        },
        success : function(data) {
            $(ele).data("key",data);
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

function moveToolToWorkBench(ele,materialName){
   $.ajax({
        url : '/moveToolToWorkBench',
        type : 'POST',
        data : {
            'materialName' : materialName
        },
        success : function(data) {
            $(ele).data("key",data);
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

//Moving material back into inventory
function removeFromWorkbench(materialName){
   $.ajax({
        url : '/moveToInventory',
        type : 'POST',
        async: false,
        data : {
            'materialName' : materialName
        },
        success : function(data) {

        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

//Pouring from beaker to beaker
// This can also be time instead of amount
function pour (mat1, mat2, amount){
   $.ajax({
        url : '/pour',
        type : 'POST',
        async: false,
        data : {
            'container1' : $(mat1).data("key").name,
            'container2' : $(mat2).data("key").name,
            'amount': amount.toFixed(2)
        },
        dataType:'json',
        success : function(data) {
            $(mat1).data("key",data[0]);
            $(mat2).data("key",data[1]);
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

function drawUp (mat1, mat2, amount){
   $.ajax({
        url : '/drawUp',
        type : 'POST',
        async: false,
        data : {
            'container1' : $(mat1).data("key").name,
            'container2' : $(mat2).data("key").name,
            'amount': amount
        },
        dataType:'json',
        success : function(data) {
            $(mat1).data("key",data[0]);
            $(mat2).data("key",data[1]);
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

function release (mat1, mat2, amount){
   $.ajax({
        url : '/drawUp',
        type : 'POST',
        async: false,
        data : {
            'container1' : $(mat1).data("key").name,
            'container2' : $(mat2).data("key").name,
            'amount': amount
        },
        dataType:'json',
        success : function(data) {
            $(mat1).data("key",data[0]);
            $(mat2).data("key",data[1]);
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}


//Mix beaker
function swirl (mat1){
   $.ajax({
        url : '/mix',
        type : 'POST',
        async: false,
        data : {
            'container1' :  $(mat1).data("key").name,
        },
        dataType:'json',
        success : function(data) {
            $(mat1).data("key",data);
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

//Heat beaker
function heat (beaker1, time){
   $.ajax({
        url : '/heat',
        type : 'POST',
        async: false,
        data : {
            'beaker1' : beaker1,
            'temp' : temp
        },
        dataType:'json',
        success : function(data) {

        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

//When lab is finished
function finishLab(){
   $.ajax({
        url : '/finishLab',
        type : 'POST',
        async: false,
        data : {
        },
        success : function(data) {
            window.location.href=data;
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

//When lab is finished
function cancelLab(){
   $.ajax({
        url : '/cancelLab',
        type : 'GET',
        async: false,
        data : {
        },
        success : function(data) {
           window.location.href=data;
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}
