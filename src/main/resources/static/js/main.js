/**
 * main.js
 * http://www.codrops.com
 *
 * Licensed under the MIT license.
 * http://www.opensource.org/licenses
 it-license.php
 *
 * Copyright 2015, Codrops
 * http://www.codrops.com
 */

var editingFlag,editingLabFlag=0;
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


$('#Solutions').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
    var value = $("#Solutions>option").map(function() { return $(this).val(); })[clickedIndex];
    if(isSelected == false){
        $('.outgroup-Sol').find(`[value=${value}]`).remove();
        $('.Container1, .Container2').selectpicker('refresh');
    }
    else{
        $('.outgroup-Sol').append(new Option(value, value));
        $('.Container1, .Container2').selectpicker('refresh');
    }
});

$('#Containers').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
    var value = $("#Containers>option").map(function() { return $(this).val(); })[clickedIndex];
    if(isSelected == false){
        $('.outgroup-Con').find(`[value=${value}]`).remove();
        $('.Container1, .Container2').selectpicker('refresh');
    }
    else{
        $('.outgroup-Con').append(new Option(value, value));
        $('.Container1, .Container2').selectpicker('refresh');
    }
});




function deleteInstruction(card){
    $(card).parents()[0].remove();
    for(var i = 0 ; i < $('.instruction_cards').children().length ; i++){
        $($('.step-number')[i]).text(i+1);
    }
}

function LoadWorkbench(labId){
     window.location.href = "/workbench?labId="+labId;
}

function LabWork(url, LabId = -1, publish = 0){
    var instructions = [];
    var container = 0;
    for(var i = 0 ; i < $('.instruction_cards').children().length ; i++){
        if(i == 1)
            container = i + 2;
        else
            container = (i * 2) + 1;
        instructions.push({
          stepNumber: $($('.step-number')[i]).text(),
          name: $($('.instruction-title')[i]).text(),
          container1: $($('.Container1')[container]).val(),
          container2: $($('.Container2')[container]).val(),
          targetTemp : $($('.targetTemp')[i]).val(),
          targetVolume: $($('.targetVolume')[i]).val()
        });
    }
    $.ajax({
        url : url,
        type : 'POST',
        async: false,
        data : {
            labId : LabId,
            published: publish,
            timeLimit : $("#timeLimit").val(),
            courseId : $(".menu__link--current").attr("value"),
            labName : $("#LabName").val(),
            labDescription : $("#LabDesc").val(),
            Solutions : $("#Solutions").val().toString(),
            Containers : $("#Containers").val().toString(),
            Tools : $("#Tools").val().toString(),
            Instructions : JSON.stringify(instructions)
        },
        success : function() {
            $(".menu__link--current")[0].click();
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}

function fetchLab(labEditButton){
        $("#Change-Lab-Header").text("Edit Lab");
        var labId  = $(labEditButton).attr("value");
        $("#Make-Edit-Lab-Button").unbind('click');
        $("#Make-Edit-Lab-Button").on("click", function(){
              LabWork('/EditLab',labId);
        });
        $("#Publish").unbind('click');
        $("#Publish").on("click", function(){
              LabWork('/EditLab',labId,1);
        });
        $("#Test-Lab").attr("onclick","LoadWorkbench("+labId+")");
        if($(".add-lab-form").is(":hidden")){
            toggleL();
        }
        editingLabFlag = 1;
        $.ajax({
            url : '/fetchLabInfo',
            type: 'GET',
            async: false,
            data : {
                'labId' : labId
            },
            dataType: 'json',
            success : function(data){
                $("#cardigans").empty();
                $("#LabName").val(data.labName);
                $("#LabDesc").val(data.labDesc);
                $(".selectpicker").selectpicker('deselectAll');
                $('#Solutions').selectpicker('val', Array.from(data.solutions, x => x.name));
                $('#Containers').selectpicker('val', Array.from(data.containers, x => x.name));
                $('#Tools').selectpicker('val', Array.from(data.tools, x => x.name));
                var instructions = data.instructions;
                instructions.sort(function(a, b) {
                    var A = a.stepNumber;
                    var B = b.stepNumber;
                    return (A < B) ? -1 : (A > B) ? 1 : 0;
                });
                for(instruction of data.instructions) {
                  cardMaker(instruction.name, 1, instruction.container1,instruction.container2,instruction.targetTemp, instruction.targetVolume);
                }
                $(".form-wrap").scrollTop(0);
            },
            error : function(request, error)
            {
                alert("Request: "+JSON.stringify(request))
            }
        });
}
function editCourse(course){
        $('#delete-course').show();
        $("#Change-Header").text("Edit Course");
        if($(".add-course-form").is(":hidden")){
            toggleC();
        }
        editingFlag = 1;
        var courseId  = course.id;
        $.ajax({
            url : '/fetchCourseInfo',
            type: 'GET',
            async: false,
            data : {
                'courseId' : courseId
            },
            dataType: 'json',
            success : function(data){
                $(".add-course-form").attr("action", actionEditCourse);
                $("#CourseName").val(data.courseName);
                $("#CourseDesc").val(data.courseDesc);
                $('#StudentList').multiSelect('deselect_all');
                $('#InstructorList').multiSelect('deselect_all');
                $(".form-wrap").scrollTop(0);
                $(".add-course-form").fadeOut("fast", function() {
                       $('.add-course-form').fadeIn( "fast" );
                });
                $('.CourseNumberDiv').empty().append('<input id="courseId" name="courseId" type="hidden" value="'+ courseId +'">')
                for(var i=0; i<data.students.length; i++){
                       $('#StudentList').multiSelect('select', data.students[i].id.toString());
                }
                for(var i=0; i<data.instructors.length; i++){
                       $('#InstructorList').multiSelect('select', data.instructors[i].id.toString());
                }
                $('#StudentList').multiSelect("refresh");
                $('#InstructorList').multiSelect("refresh");
            },
            error : function(request, error)
            {
                alert("Request: "+JSON.stringify(request))
            }
        });
}
function duplicateLab(x){
    id = $(x).parents()[1].id;
    $.ajax({
        url : '/DuplicateLab',
        type : 'POST',
        async: false,
        data : {
            'labId' : id,
            'courseId' : $(".menu__link--current").attr("value")
        },
        success : function(data) {
            $(".menu__link--current")[0].click();
        },
        error : function(request,error)
        {
            alert("Request: "+JSON.stringify(request));
        }
    });
}
function deleteLab(x){
    id = $(x).parents()[1].id;
    $.confirm({
            title: 'Confirmation',
            content: 'Are you sure you want to delete this Lab?',
            autoClose: 'cancel|8000',
                buttons: {
                    deleteUser: {
                        text: 'Delete Lab',
                        action: function () {
                            $.ajax({
                                url : '/DeleteLab',
                                type : 'POST',
                                async: false,
                                data : {
                                    'labId' : id,
                                    'courseId' : $(".menu__link--current").attr("value")
                                },
                                success : function(data) {
                                    var obj = $(".menu__link--current");
                                    $(".menu__link--current")[0].click();
                                },
                                error : function(request,error)
                                {
                                    alert("Request: "+JSON.stringify(request));
                                }
                            });
                        }
                    },
                    cancel: function () {
                    }
                }
        });
}
function cardMaker(cardHeader, fetchflag = 0, selCon1 = "", selCon2 = "", targetTemp = -1, targetVolume = -1) {
        var newCardNumber = $('.instruction_cards').children().length;
        var html = '<div class="card instruction">'+
                   '<button type="button" class="close" onclick="deleteInstruction(this)" aria-label="Close"><span aria-hidden="true">×</span></button>'+
                   '<div class="card-body">'+
                   '<p class="step-number">'+(newCardNumber+1)+'</p>'+
                   '<h4 class="instruction-title">'+cardHeader+'</h4>'+
                   '<input class="instructionNames" type="hidden" value="'+cardHeader+'">';
            html += '<select class="selectpicker Container1" data-width="100%" data-container="body"><optgroup class="outgroup-Sol" label="Solutions">';
                for(x of $('#Solutions').val()){
                    html += '<option value="'+x+'">'+x+'</option>';
                }
            html +='</outgroup><optgroup class="outgroup-Con" label="Containers">';
                for(x of $('#Containers').val()){
                    html += '<option value="'+x+'">'+x+'</option>';
                }
            html +='</outgroup></select></br>';

            html += '<select class="selectpicker Container2" data-width="100%" data-container="body"><optgroup class="outgroup-Sol" label="Solutions">';
                for(x of $('#Solutions').val()){
                    html += '<option value="'+x+'">'+x+'</option>';
                }
            html +='</outgroup><optgroup class="outgroup-Con" label="Containers">';
                for(x of $('#Containers').val()){
                    html += '<option value="'+x+'">'+x+'</option>';
                }
            html +='</outgroup></select>';

            html +=  '<div class="input-group mb-2 targetTempDiv">'+
                     '<input type="number" class="form-control targetTemp">'+
                     '<div class="input-group-append"><div class="input-group-text">°C</div></div></div>';
            html +=  '<div class="input-group mb-2 targetVolumeDiv">'+
                     '<input type="number" class="form-control targetVolume">'+
                     '<div class="input-group-append"><div class="input-group-text">mL</div></div></div>';
            html += '</div></div>';

        $('.instruction_cards').append(html);
        $(".selectpicker").selectpicker('refresh');
        newCardNumber = $('.instruction_cards').children().length - 1;
        var card = $('.instruction_cards').children()[newCardNumber];
        if(cardHeader == "Mix" || cardHeader == "Transfer"){
            if(fetchflag==1){
                $($(card).find(".Container1")[1]).selectpicker('val', selCon1);
                $($(card).find(".Container2")[1]).selectpicker('val', selCon2);
            }
            $(card).find(".targetTempDiv").hide();
            $(card).find(".targetVolumeDiv").hide();
        }
        else if(cardHeader == "Weigh" || cardHeader == "Swirl" || cardHeader == "Rinse"){
            if(fetchflag==1){
                $($(card).find(".Container1")[1]).selectpicker('val', selCon1);
            }
            $($(card).find(".Container2")[1]).selectpicker('hide');
            $($(card).find(".Container2")[1]).children().remove();
            $(card).find(".targetTempDiv").hide();
            $(card).find(".targetVolumeDiv").hide();
        }
        else if(cardHeader == "Heat" || cardHeader == "Cool"){
            if(fetchflag==1){
                $($(card).find(".Container1")[1]).selectpicker('val', selCon1);
                $(card).find(".targetTemp").val(targetTemp);
            }
            $($(card).find(".Container2")[1]).selectpicker('hide');
            $($(card).find(".Container2")[1]).children().remove();
            $(card).find(".targetVolumeDiv").hide();
        }
        else if(cardHeader == "Draw Up"){
            if(fetchflag==1){
                $($(card).find(".Container1")[1]).selectpicker('val', selCon1);
                $(card).find(".targetVolume").val(targetVolume);
            }
            $($(card).find(".Container2")[1]).selectpicker('hide');
            $($(card).find(".Container2")[1]).children().remove();
            $(card).find(".targetTempDiv").hide();
        }
    }
function toggleL() {
	    if($(".add-lab-form").is(":visible")){
	        $('.add-lab-form').fadeOut( "fast" , function() {
                $(".tabs-visb").fadeIn( "fast");
            });
	    }
	    else{
            if($(".add-course-form").is(":visible")){
                $(".add-course-form").fadeOut( "fast", function() {
                    $('.add-lab-form').fadeIn( "fast" );
                });
                $(".tabs-visb").fadeOut("fast");
            }else{
                $(".tabs-visb").fadeOut("fast", function() {
                       $('.add-lab-form').fadeIn( "fast" );
                });
            }
	    }
	}
function toggleC() {
        if($(".add-course-form").is(":visible")){
            $('.add-course-form ').fadeOut( "fast" , function() {
                $(".tabs-visb").fadeIn( "fast");
            });
        }
        else{
            if($(".add-lab-form").is(":visible")){
                $(".add-lab-form").fadeOut( "fast", function() {
                    $('.add-course-form').fadeIn( "fast" );
                });
                $(".tabs-visb").fadeOut("fast");
            }else{
                $(".tabs-visb").fadeOut("fast", function() {
                       $('.add-course-form').fadeIn( "fast" );
                    });
            }
        }
    }

$('.addl_btn').click(function(){
    $("#Change-Lab-Header").text("Add Lab");
    $("#Make-Edit-Lab-Button").unbind('click');
    $("#Make-Edit-Lab-Button").on("click", function(){
      LabWork('/MakeLab');
    });
    $("#Publish").unbind('click');
    $("#Publish").on("click", function(){
          LabWork('/EditLab',labId,1);
    });
    $("#LabName").val("");
    $("#LabDesc").val("");
    $(".selectpicker").selectpicker('deselectAll');
    $("#cardigans").empty();
    if(editingLabFlag!=1)
        toggleL();
    else{
         editingLabFlag = 0;
         $(".form-wrap").scrollTop(0);
         $(".add-lab-form").fadeOut("fast", function() {
               $('.add-lab-form').fadeIn( "fast" );
        });
    }
});

$('.addc_btn').click(function(){
    $("#Change-Header").text("Add Course");
    $('#delete-course').hide();
    $(".add-course-form").attr("action", actionMakeCourse);
    $("#CourseName").val("");
    $("#CourseDesc").val("");
    $('#StudentList').multiSelect('deselect_all');
    $('#InstructorList').multiSelect('deselect_all');
    if(editingFlag!=1)
        toggleC();
    else{
         editingFlag = 0;
         $(".form-wrap").scrollTop(0);
         $(".add-course-form").fadeOut("fast", function() {
               $('.add-course-form').fadeIn( "fast" );
        });
    }
});

$('#delete-course').click(function(){
    $.confirm({
        title: 'Confirmation',
        content: 'Are you sure you want to delete this course?',
        autoClose: 'cancel|8000',
            buttons: {
                deleteUser: {
                    text: 'Delete Course',
                    action: function () {
                        $.ajax({
                                url : '/DeleteCourse',
                                type : 'POST',
                                async: false,
                                data : {
                                    courseId : $("#courseId").attr("value"),
                                },
                                success : function(data) {
                                    location.reload();
                                    $.alert('Course has been deleted!');
                                },
                                error : function(request,error)
                                {
                                     $.alert('There was an error while deleting the course! Please try again.');
                                }
                            });
                    }
                },
                cancel: function () {
                }
            }
    });
});



$(document).ready(function() {

    var students,instructors;
    //This gets the email from the front end and passes calls the loadCourses function with this email.
    initialize();
    $( ".addl_btn" ).prop( "disabled", true );

    $(".dropdown-item").on('click', function(event){
        event.stopPropagation();
        event.stopImmediatePropagation();
        cardMaker(this.value);
    })

    $(".instruction_cards").mousewheel(function(event, delta) {
               this.scrollLeft -= (delta * 50);
               this.scrollRight -= (delta * 50);
               this.style.transition = '1s';
               event.preventDefault();
    });

        function initialize() {
        		var menuEl = document.getElementById('ml-menu'),
        			mlmenu = new MLMenu(menuEl, {
        				// breadcrumbsCtrl : true, // show breadcrumbs
        				// initialBreadcrumb : 'all', // initial breadcrumb text
        				breadcrumbsCtrl : false,
        				backCtrl : false, // show back button
        				// itemsDelayInterval : 60, // delay between each menu item sliding animation
        				onItemClick: loadLabs // callback: item that doesn´t have a submenu gets clicked - onItemClick([event], [inner HTML of the clicked item])
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

        		// simulate grid content loading
        		var gridWrapper1 = document.querySelector('#saved');
        		var gridWrapper2 = document.querySelector('#published');

                function loadLabs(ev,itemName){
                    if($(".add-course-form").is(":visible")){
                        toggleC();
                    }
                    else if($(".add-lab-form").is(":visible")){
                        toggleL();
                    }
                    $.ajax({
                        url : '/loadLabs',
                        type : 'GET',
                        async: false,
                        data : {
                            'courseName' : itemName
                        },
                        dataType:'json',
                        success : function(data) {
                                $( ".addl_btn" ).prop( "disabled", false );
                                ev.preventDefault();
                                closeMenu();
                                gridWrapper1.innerHTML = '';
                                gridWrapper2.innerHTML = '';
                                classie.add(gridWrapper1, 'content--loading');
                                classie.add(gridWrapper2, 'content--loading');
                                setTimeout(function() {
                                    classie.remove(gridWrapper1, 'content--loading');
                                    var saved = published = '<ul class="products">';
                                    for(var i = 0;i<data.length;i++){
                                        var lab = data[i];
                                        if(lab.published)
                                            published+= dummyData["cardbody1"]+lab.labId+dummyData["cardbody2"]+lab.labName + dummyData["cardbody3"] + lab.labDesc + dummyData["cardbody4"]+lab.labId+dummyData["cardbody5"];
                                        else
                                            saved+= dummyData["cardbody1"]+lab.labId+dummyData["cardbody2"]+lab.labName + dummyData["cardbody3"] + lab.labDesc + dummyData["cardbody4"]+lab.labId+dummyData["cardbody5"];
                                      }
                                    saved +="</ul>";
                                    published +="</ul>";
                                    gridWrapper1.innerHTML = saved;
                                    classie.remove(gridWrapper2, 'content--loading');
                                    gridWrapper2.innerHTML = published;
                                }, 700);
                        },
                        error : function(request,error)
                        {
                            alert("Request: "+JSON.stringify(request));
                        }
                    });
                }
        }
});


