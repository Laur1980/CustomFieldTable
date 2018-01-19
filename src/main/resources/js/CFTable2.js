
function checkContainer(type, mainContainer, iter){
	console.log("++++++INSIDE checkContainer++++++");
	console.log("==========> type: ",type);
	console.log("==========> mainContainer: ",mainContainer);
	console.log("==========> iter: ",iter);
	var container; //table
	var isVisible1=false;
	var isVisible2=false;
	var mainContainerCP = mainContainer;
	var elem1, elem1;
	 if(mainContainer != null){
	 	console.log("==> Search in Popup");
	 	elem_1 = mainContainer.find('div#tableContainer2');
	 	elem_2 = mainContainer.find('table.customTable2');
	 } else {
	 	console.log("==> Search in entire document");	
	 	elem_1 = AJS.$('div#tableContainer2');
	 	elem_2 = AJS.$('table.customTable2');
	 }
	
	if(type && typeof type != "undefined"){
		switch(type){
				case"view":
							        container = AJS.$('div#tableContainer2');
							       isVisible1 = container.is(':visible');
							       break;
				default:	container = AJS.$('table.customTable2');
									isVisible2 = container.is(':visible');
		}
		
		console.log("===>After if1: container: ",container);
		console.log("===>After if1: isVisible1(view): ",isVisible1);
		console.log("===>After if1: isVisible2(edit): ",isVisible2);
				
	}
	
	if(container && typeof container != "undefined" && container.is(':visible')){
			console.log("===>Container IS visible");
			AJS.$('tr td',container).each(
					function(){
						AJS.$(this).parent().remove();
					}
			);
			
			switch(type){
				case'view':console.log("=======> if2, view case");
										//parcurgere input 
									break;
				default:console.log("=======> if2, edit case");
								//parcurgere input si editare
								parseTableAndEditTable(container);						
			}
		
	}else{
		console.log("===>Container IS NOT visible");
		var elem1_row = AJS.$('tr', elem_1); //editable element 1 from current row
		var elem2_row = AJS.$('tr', elem_2);//editable element 2 from current row
		console.log("===>elem1_row", elem1_row);
		console.log("===>elem2_row", elem2_row);
		var lenTh1 =  AJS.$('th',elem1_row ).length;
		var lenTh2 =  AJS.$('th',elem2_row ).length;
		console.log("===>lenTh1", lenTh1);
		console.log("===>lenTh2", lenTh2);
		var lenTd1 =  AJS.$('td',elem1_row ).length;
		var lenTd2 =  AJS.$('td',elem2_row ).length;
		console.log("===>lenTd1", lenTd1);
		console.log("===>lenTd2", lenTd2);
		
		if(lenTh1 != 0 || lenTh2 != 0){
			if(lenTd1 == 0 && lenTd2 == 0){
				console.log("Container timeout...");
					setTimeout(function(){
									if(iter > 0){
										--iter;
										checkContainer('edit', mainContainerCP, iter);
										checkContainer('view', mainContainerCP, iter);
									}else{
										console.log("==> Finish number of iterations for table finding!")
									}
						}, 1000);
			}
		}else{
			setTimeout(function(){
				if(iter > 0){
					--iter;
					checkContainer('edit', mainContainerCP, iter);
					checkContainer('view', mainContainerCP, iter);
				}else{
					console.log("==> Finish number of iterations for table finding!")
				}
			}, 1000);
			
		}
		
	}
	
	console.log("++++++GETTING OUT OF  checkContainer++++++");
}

var tipDeSuma = {"p":"Pieredere","c":"Castig" };
var numeTipDeSuma = ['Pieredere',"Castig" ];

function parseTableAndEditTable(container){
	
	console.log("++++++parseTableAndEditTable++++++");
	var  rowTemplate=[
													{"type":"text","name":"Suma"},
													{"type":"select","name":"Tip de suma", "value":"pierdere|castig"},
													{"type":"date","name":"date"}
											];
	var input = AJS.$('textarea.tableHidden_TA2');
	var rowInput = AJS.$('textarea.tableHidden_TA2').text();
	console.log('==>rowInput: ',rowInput);
	var table = container;
	console.log('==>container: ',container);
	var numberForRow = table.find('th').length -2 ;
	console.log('==>numberForRow: ',numberForRow);
	var elemArr = [];
	var totalSum = 0;
	
		if(typeof rowInput != "undefined" && rowInput != ""){
			
			elemArr = rowInput.split("(,)");
			console.log('==>elemArr: ',elemArr);
			//parsing
			elemArr.forEach(
													function (elem){
												
														var rowElementsArr = elem.split("(|)");
														var lastRowID = Number(table.find('tr:last-of-type').data('id'))+1;
														var rowElem = AJS.$('<tr/>').data('id',lastRowID);
														
														rowElem.append(AJS.$('<td/>').text(lastRowID));
														
														var i ;//counter
														
														for(i = 0;i<numberForRow;i++){
															switch(i){
																case 1:rowElem.append(AJS.$('<td/>').text(getNumberFromStr(elemArr[i])));break;
																case 2:rowElem.append(AJS.$('<td/>').text(numeTipDeSuma[elemArr[i]]));break;
																default:rowElem.append(AJS.$('<td/>').text(pretifyString(elemArr[i])));
															}
														}
														var minusSignCell = AJS.$('<td/>').append( AJS.$('<p/>')
																																	.text("Del.").prop('title','Stergere linie')
																																	.addClass('closeBtn').css({"cursor": "pointer"})
																																	.bind('click', {'rowTemplate':rowTemplate, 'table':table, 'row': rowElem, 'input': input}, deleteRow) );
														rowElem.append(minusSignCell);
														table.append(rowElem);
													});
		}
		
		//adding new input
		var tempInputRow = AJS.$('<tr/>').addClass('inputRow').append( AJS.$('<td/>'));
		
		var j ;//counter
		
		for(j =0;j<numberForRow;j++){
				
			var cellElement = AJS.$('<td/>');
			var inputElement ;
			
			if(rowTemplate[j].type == "text"){
				
						inputElement = AJS.$('<input/>', {'type':'text', 'name': rowTemplate[j].name});
						if(rowTemplate[j].name == "suma"){
							inputElement.css('width', '50px');
						}
						
			}else if(rowTemplate[j].type == "date"){
					var currDate = new Date();
					var currMonth = currDate.getMonth();
					var currDay = currDate.getDate();
					var currYear = currDate.getFullYear();
					var newDateVal = ('0'+currDay).slice(-2)+"/"+('0'+(currMonth+1)).slice(-2)+"/"+currYear;
					
					inputElem = AJS.$('<input/>', {'type': 'text', 'name': rowTemplate[j].name, 'value': newDateVal});
					inputElem.css({'text-align':'center', 'width':'100px'});
			}else{
				
				inputElement = AJS.$('<select/>', {'name':rowTemplate[j].name});
				rowTemplate[j]['value'].split("|").forEach(
																											function(elem, index){
																																		var option, value;
																																			if(index == 0){
																																				
																																					//???
																																						if(rowTemplate[j].hasOwnProperty('sumType')){
																																							value = rowTemplate[j]['sumType'][elem];
																																						}else{ 
																																							value = pretifyString(elem);
																																						}
																																							
																																						if(rowTemplate[j].name == "Tip de suma"){
																																							console.log('===>Tip de suma: ', rowTemplate[j].name);
																																							option = AJS.$('<option/>', {'value':elem, 'selected': 'selected'}).text( numeTipDeSuma[value] );
																																						} else {
																																							option = AJS.$('<option/>', {'value':elem, 'selected': 'selected'}).text( value );
																																						}
																														
																																			}else{
																																							//++++ Continue HERE+++++
																																							//???
																																							if(rowTemplate[j].hasOwnProperty('sumType')){
																																								value = rowTemplate[j]['sumType'][elem];
																																							}else{
																																							   value = pretifyString(elem);
																																							}
																																			}
																																			inputElement.append(option);
																											
																									});
			}
			tempInputRow.append( cellElement.append(inputElement) );
		}
		tempInputRow.append( AJS.$('<td/>')
				.append( AJS.$('<p/>').text('Ad.').prop('title', 'Adauga linie')
				.addClass('addBtn').css("cursor", "pointer").bind("click", {'table': table, 'row': tempInputRow, 'input': input}, makeRow) ) );
		table.append(tempInputRow);
	
}

function deleteRow(event){
	console.log('++++++++INSIDE deleteRow++++++++');
	var table = event.data['table'];
	console.log("==>table object",table);
	var row = event.data['row'];
	var inputElem = event.data['input'];
	
	var rowID = Number(row.data('id'));
	
	var splittedArr = inputElem.text.split("(,)");
	
	var newValue = "";
	if(splittedArr.length != 1){
		splittedArr.splice(rowID-1, 1);
		newValue = splittedArr.join('(,)'); 
	}
	inputElem.text( newValue );

	row.remove();
	console.log('++++++++END deleteRow++++++++');
}

function makeRow(event){
	console.log('++++++++INSIDE makeRow++++++++');
	var table = event.data['table'];
	console.log('==>table: ',table);
	var lastRowID = Number(table.find('tr:last-of-type').prev().data('id')) + 1;
	console.log('==>lastRowID: ',lastRowID);
	var row = event.data['row'];
	console.log('==>row: ',row);
	var inputElem = event.data['input'];
	console.log('==>inputElem: ',inputElem);
	var nrOfCol = table.find('th').length - 2;
	console.log('==>nrOfCol: ',nrOfCol);
	var elements = [];

	var newRow = AJS.$('<tr/>').data('id', lastRowID).append( AJS.$('<td/>').text(lastRowID) );
	console.log('==>newRow: ',newRow);
	var appendedVal, inputValHolder;
	
	var k;//counter
	
	for(k = 1;k<=nrOfCol;k++){
			var inputTemp = AJS.$('td', row).eq(k).find('input');
			inputValHolder = "";
			if(inputTemp.length == 0){
					 inputTemp = AJS.$('td',row).eq(k).find('select');
					//numeTipDeSuma
					 if(numeTipDeSuma.hasOwnProperty(inputTemp.val())){
						 newRow$.append( AJS.$('<td/>').text(sumType[inputTemp.val()] ) );
						 inputValHolder = inputTemp.val();								 
					 }else{
						 newRow.append( AJS.$('<td/>').text( pretifyString(inputTemp.val()) ) );
						 inputValHolder = inputTemp.val();
					 }
			}else{
				if(k == 3){ // date column
						var tempVal = inputTemp.val();
						if(tempVal.trim().length == 0){
							inputTemp.val('').addClass('wrongInput')
																	 .bind('click', function (){
																		 														AJS.$(this).removeClass('wrongInput'); 
																	});
							return;
						}else{
								//formating data
								var dateArray = tempVal.split('/');
								if(dateArray.length != 3){
									inputTemp.val('').addClass('wrongInput')
																		  .bind('click', function (){
																			 														AJS.$(this).removeClass('wrongInput'); 
																		 	});
											return;
								}
								
								var isValidDate = true;
								var tempDay = Number(dateArr[0]);
								var tempMonth =  Number(dateArr[1]);
								var tempYear = Number(dateArr[2]);
								var inputDate = new Date(tempYear, tempMonth-1, tempDay);
								if(typeof tempDay == "undefined" || typeof tempMonth == "undefined" || typeof tempYear == "undefined"){
									isValidDate = false;
								}else if(tempMonth <= 0 || tempMonth > 12 ){ //Month verify
									isValidDate = false;
								}else if(tempDay <= 0 || tempDay > 31){//Day verify
									isValidDate = false;
								}else if(tempYear > new Date().getFullYear()){ //if year is in future
									isValidDate = false;
								}else if(inputDate > new Date()){ //If current date is lower then input date then -> error
									isValidDate = false;
								}
								if(isValidDate == false){
									iinputTemp.val('').addClass('wrongInput')
									  .bind('click', function (){
																								AJS.$(this).removeClass('wrongInput'); 
																					  		});
									return;			
								}else{
									tempVal =  ('0' + inputDate.getDate()).slice(-2) +"/"+ ('0' + (inputDate.getMonth()+1)).slice(-2) +"/"+ inputDate.getFullYear(); 
								}
								tempVal = tempVal.replace(/\s/, "");
								
						}
						appendedVal = pretifyString( tempVal );
						inputValHolder = tempVal;
				
				}else if(k == 1){
					appendedVal = getNumberFromStr(inputTemp.val());
					if(appendedVal == 0){
						inputTemp.val('').addClass('wrongInput').bind('click', function (){ AJS.$(this).removeClass('wrongInput'); });
						return;
					}
				inputValHolder = appendedVal;
			}
			newRow.append( AJS.$('<td/>').text( appendedVal ) );
	}
	elements.push( inputValHolder );
	console.log('++++++++END makeRow++++++++');
	}
	newRow.append( AJS.$('<td/>').append( AJS.$('<p/>').text('Del.').prop('title', 'Stergere linie').addClass('closeBtn').css("cursor", "pointer").bind('click', {'table':table, 'row': newRow$, 'input': inputElem$}, deleteRow) ));

	table.find('tr.inputRow').before( newRow );
	
	row.find('input').val('');
	//Fill date input with current date
	var currDate = new Date();
	var currDay = currDate.getDate();
	var currMonth = currDate.getMonth();
	var currYear = currDate.getFullYear();
	var newDate =  ('0' + currDay).slice(-2) +"/"+ ('0' + (currMonth+1)).slice(-2) +"/"+ currYear ;
	row.find('input').last().val( newDate );
	AJS.$('select', row).each(function(){
		AJS.$(this).val( AJS.$(this).find('option:first').val() );
	});

	if(inputElem.text().length != 0){
		inputElem.text(inputElem.text()+ "(,)" + elements.join("(|)"));
	}else{
		inputElem.text(elements.join('(|)'));
	}
}

function getNumberFromStr(str){
	if(str){
		return Number(str)||0;
	}else if(typeof str == 'undefined' || str==""){
		return 0;
	}
}

function pretifyString(str){
	
	if(typeof str=="undefined"){
		return;
	}
	
	str = str.trim();
	if(!isNaN(parseInt(str.charAt(0)) ) ) 
		return str;
	return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();	
}

function generateSuccNumbers(nr){
	var arr = [];
	for(var idx = 0; idx < nr; idx++){
		arr.push(idx);
	}

	return arr;
}
