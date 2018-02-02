

//When "main container" is null it means that the item is searched in entire document when is != null then searching is made on popup window
function checkContainer(type, mainContainer, iter){
	var container;
	var isVisible1=false, isVisible2=false ;
	var mainContainerCP = mainContainer;
		
	// console.log("Type: " + type);

	var elem_1, elem_2;
	// if(mainContainer != null){
	// 	console.log("==> Search in Popup");
	// 	elem_1 = mainContainer.find('div#tableContainer');
	// 	elem_2 = mainContainer.find('table.customTable');
	// } else {
	// 	console.log("==> Search in entire document");	
	// 	elem_1 = AJS.$('div#tableContainer');
	// 	elem_2 = AJS.$('table.customTable');
	// }

	if(type && typeof type != "undefined"){
		switch(type){
			case "view":
				container = AJS.$('div#tableContainer');
				isVisible1 = container.is(":visible");
				break;
			case "edit":
				container = AJS.$('table.customTable');
				isVisible2 = container.is(":visible");
				break;
		}
	}else{
		console.log("Type undefined exiting...");
	} 


	if(container && typeof container != "undefined" && container.is(":visible")){
		AJS.$('tr td', container).each(function(){
			AJS.$(this).parent().remove();
		});

		switch(type){
			case "view":
				parseInput(container);
				break;
			case "edit":
				parseInputAndEdit(container);
				break;	
		}
	}else{
		var elem1_row = AJS.$('tr', elem_1); 
		var elem2_row = AJS.$('tr', elem_2);

		if(AJS.$('th', elem1_row).length != 0 || AJS.$('th', elem2_row).length != 0){
			if(AJS.$('td', elem1_row).length == 0 && AJS.$('td', elem2_row).length == 0){
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
}

var sumType = {"prov":"Pierdere", "recup":"Castig"};
var statSumNames = ['Contabilizat in pierdere castig', 'Provizion', 'Estimat', 'Pierdere de PNB nerecuperabila', 'Recuperare din asigurari contabilizata',
					 'Recuperare din asigurari estimata', 'Recuperare de la tert contabilizata', 'Recuperare de la tert estimata', 'Reluare provizion'];


function parseInput(container){
	var input = container.find('textarea.tableHiddenTA1').text();
	var table$ = container.find('table');
	var valuesNr = table$.find('th').length - 1;
	var elemArr = input.split("(,)");

	if(input.length != 0){	
		elemArr.forEach(function(elem){
			var rowElemsArr = elem.split("(|)");
			var lastRowID = Number(table$.find('tr:last-of-type').data('id')) + 1;
			var row$ = AJS.$('<tr/>').data('id', lastRowID);
			row$.append( AJS.$('<td/>').text(lastRowID) );
			
			for(var idx=0; idx < valuesNr; idx++){
				
				switch(idx){
					case 1: //Tip impact
						row$.append( AJS.$('<td/>').text( pretifyString(sumType[rowElemsArr[idx]])) );
						break;
					case 2: //Statut suma
						row$.append( AJS.$('<td/>').text( statSumNames[rowElemsArr[idx]] ) );
						break;
					case 4: //Suma
						row$.append( AJS.$('<td/>').text( getNumberFromStr(rowElemsArr[idx] ) ) );	
						break;
					default: //Rest
						row$.append( AJS.$('<td/>').text(pretifyString(rowElemsArr[idx])));	
				}
			}

			table$.append(row$);
		});
	}	
}

function parseInputAndEdit(container){

	//Create new row data
	var rowTemplate = [{"type": "text", "name":"denumire"}, 
						{"type":"select", "val":"prov|recup", "name": "tipSuma", "sumType": sumType}, 
						{"type":"select", "val": "" + generateSuccNumbers(statSumNames.length).join('|'), "name": "statutSuma"}, 
						{"type":"date", "name": "dataconta"}, //Data contabilizare
						{"type":"text", "name":"suma"}, 
						{"type":"select", "val":"ron|usd|eur", "name":"valuta"}, 
						{"type":"date", "name":"date"}];


	//Container means table in this case 
	var input$ = AJS.$('textarea.tableHiddenTA2');
	var input = input$.text();
	var table$ = container;
	var valuesNr = table$.find('th').length - 2;
	var elemArr = [];
	var totalSum = 0;

	if(typeof input != "undefined" && input != ""){ 

		elemArr = input.split("(,)");

		totalSum = 0;
		
		//Acutal parsing		
		elemArr.forEach(function(elem){
			var rowElemsArr = elem.split("(|)");
			var lastRowID =Number(table$.find('tr:last-of-type').data('id')) + 1;
			var row$ = AJS.$('<tr/>').data('id', lastRowID);
			
			row$.append( AJS.$('<td/>').text(lastRowID) );
			
			for(var idx=0; idx < valuesNr; idx++){
				switch(idx){
					case 1: //Tip impact
						row$.append( AJS.$('<td/>').text( pretifyString(sumType[rowElemsArr[idx]])) );
						break;
					case 2: //Statut suma
						row$.append( AJS.$('<td/>').text( statSumNames[rowElemsArr[idx]] ) );
						break;
					case 4: //Suma
						row$.append( AJS.$('<td/>').text( getNumberFromStr(rowElemsArr[idx] ) ) );	
						break;
					default: //Rest
						row$.append( AJS.$('<td/>').text(pretifyString(rowElemsArr[idx])));
					}
			}

			var minusSignCell$ = AJS.$('<td/>').append( AJS.$('<p/>').text("Del.").prop('title','Stergere linie').addClass('closeBtn').css({"cursor": "pointer"}).bind('click', {'rowTemplate':rowTemplate, 'table':table$, 'row': row$, 'input': input$}, deleteRow) );
			row$.append(minusSignCell$);
			table$.append(row$);
		});
	}


	// New-Input 
	var tempInptRow$ = AJS.$('<tr/>').addClass('inputRow').append( AJS.$('<td/>'));
	for(var idx=0; idx < valuesNr; idx++){
		var cellElem$ = AJS.$('<td/>');
		var inputElem$;
		if(rowTemplate[idx].type == "text"){
			inputElem$ = AJS.$('<input/>', {'type':'text', 'name': rowTemplate[idx].name});
			if(rowTemplate[idx].name == "suma"){
				inputElem$.css('width', '50px');
			}
		}else if(rowTemplate[idx].type == "date"){
			var currDate = new Date();
			var currMonth = currDate.getMonth();
			var currDay = currDate.getDate();
			var currYear = currDate.getFullYear();
			var newDateVal = ('0'+currDay).slice(-2)+"/"+('0'+(currMonth+1)).slice(-2)+"/"+currYear;
			
			inputElem$ = AJS.$('<input/>', {'type': 'text', 'name': rowTemplate[idx].name, 'value': newDateVal});
			inputElem$.css({'text-align':'center', 'width':'100px'});
		} else {

			// console.log("==> Name and value " + rowTemplate[idx].name + " - " + rowTemplate[idx].val);

			inputElem$ = AJS.$('<select/>', {'name':rowTemplate[idx].name});
			rowTemplate[idx]['val'].split("|").forEach(function(elem, index){
				var option$, value;
				if(index == 0){
					if(rowTemplate[idx].hasOwnProperty('sumType'))
						value = rowTemplate[idx]['sumType'][elem];
					else 
						value = pretifyString(elem);

					if(rowTemplate[idx].name == "statutSuma"){
						option$ = AJS.$('<option/>', {'value':elem, 'selected': 'selected'}).text( statSumNames[value] );
					} else {
						option$ = AJS.$('<option/>', {'value':elem, 'selected': 'selected'}).text( value );
					}

				}else{
					if(rowTemplate[idx].hasOwnProperty('sumType'))
						value = rowTemplate[idx]['sumType'][elem];
					else
						value = pretifyString(elem);
					
					if(rowTemplate[idx].name == "statutSuma"){
						option$ = AJS.$('<option/>', {'value':elem}).text( statSumNames[value] );
					} else {
						option$ = AJS.$('<option/>', {'value':elem}).text( value );
					}
				}
				inputElem$.append( option$ );
			});
		}
		
		tempInptRow$.append( cellElem$.append(inputElem$) );
	}
	tempInptRow$.append( AJS.$('<td/>').append( AJS.$('<p/>').text('Ad.').prop('title', 'Adauga linie').addClass('addBtn').css("cursor", "pointer").bind("click", {'table': table$, 'row': tempInptRow$, 'input': input$}, makeRow) ) );
	
	table$.append(tempInptRow$);
}

function deleteRow(event){

	var table$ = event.data['table'];
	var row$ = event.data['row'];
	var inputElem$ = event.data['input'];

	var rowID = Number(row$.data('id'));

	var splittedArr = inputElem$.text().split("(,)");

	console.log("Deleting elem with index -> " + rowID);

	var newVal = "";
	if(splittedArr.length != 1){
		splittedArr.splice(rowID-1, 1);
		newVal = splittedArr.join('(,)'); 
	}
	inputElem$.text( newVal );

	row$.remove();


}

function makeRow(event){
	var table$ = event.data['table'];
	var lastRowID = Number(table$.find('tr:last-of-type').prev().data('id')) + 1;

	console.log('Row id: ' + lastRowID);
	
	var row$ = event.data['row'];
	var inputElem$ = event.data['input'];
	var nrOfCol = table$.find('th').length - 2;
	var elements = [];

	var newRow$ = AJS.$('<tr/>').data('id', lastRowID).append( AJS.$('<td/>').text(lastRowID) );
	var appendedVal, inputValHolder;

	for(var idx=1; idx <= nrOfCol; idx++){
		var inputTemp$ = AJS.$('td', row$).eq(idx).find('input');
		inputValHolder = "";
		if(inputTemp$.length == 0){
			inputTemp$ = AJS.$('td', row$).eq(idx).find('select');
			if(sumType.hasOwnProperty(inputTemp$.val())){
				newRow$.append( AJS.$('<td/>').text(sumType[inputTemp$.val()] ) );
				inputValHolder = inputTemp$.val();
			}else if(idx == 3){ //Statut suma
				newRow$.append( AJS.$('<td/>').text( statSumNames[inputTemp$.val()]) );
				inputValHolder = inputTemp$.val();
			}else{
				newRow$.append( AJS.$('<td/>').text( pretifyString(inputTemp$.val()) ) );
				inputValHolder = inputTemp$.val();
			}
		}else{
			if(idx == 1 || idx == 4 || idx == 7 ){ //Denumire sau Data sau Data contabilizare
				var tempVal = inputTemp$.val();
				if(tempVal.trim().length == 0){
					inputTemp$.val('').addClass('wrongInput').bind('click', function (){ AJS.$(this).removeClass('wrongInput'); });
					return;
				} else {
					if(idx == 4 || idx == 7){ //Data -> format verify
						var dateArr = tempVal.split('/');		
						if(dateArr.length != 3){
							inputTemp$.val('').addClass('wrongInput').bind('click', function (){ AJS.$(this).removeClass('wrongInput'); });
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
							inputTemp$.val('').addClass('wrongInput').bind('click', function (){ AJS.$(this).removeClass('wrongInput'); });
							return;			
						}else{
							tempVal =  ('0' + inputDate.getDate()).slice(-2) +"/"+ ('0' + (inputDate.getMonth()+1)).slice(-2) +"/"+ inputDate.getFullYear(); 
						}
						tempVal = tempVal.replace(/\s/, "");
					}
 					appendedVal = pretifyString( tempVal );
					inputValHolder = tempVal;
				}
				
			} else {
				appendedVal = getNumberFromStr(inputTemp$.val());
				if(appendedVal == 0){
					inputTemp$.val('').addClass('wrongInput').bind('click', function (){ AJS.$(this).removeClass('wrongInput'); });
					return;
				}
				inputValHolder = appendedVal;
			}

			newRow$.append( AJS.$('<td/>').text( appendedVal ) );
		}

		elements.push( inputValHolder );
	}

	newRow$.append( AJS.$('<td/>').append( AJS.$('<p/>').text('Del.').prop('title', 'Stergere linie').addClass('closeBtn').css("cursor", "pointer").bind('click', {'table':table$, 'row': newRow$, 'input': inputElem$}, deleteRow) ));

	table$.find('tr.inputRow').before( newRow$ );

	row$.find('input').val('');
	//Fill date input with current date
	var currDate = new Date();
	var currDay = currDate.getDate();
	var currMonth = currDate.getMonth();
	var currYear = currDate.getFullYear();
	var newDate =  ('0' + currDay).slice(-2) +"/"+ ('0' + (currMonth+1)).slice(-2) +"/"+ currYear ;
	row$.find('input').last().val( newDate );
	AJS.$('select', row$).each(function(){
		AJS.$(this).val( AJS.$(this).find('option:first').val() );
	});

	if(inputElem$.text().length != 0)
		inputElem$.text(inputElem$.text()+ "(,)" + elements.join("(|)"));
	else
		inputElem$.text(elements.join('(|)'));
	

	// recalcSum(table$, inputElem$.val());
}

function recalcSum(table$, newVal){
	var mainSumRow$ = table$.find('tr#mainSum');
	var nrOfCol = table$.find('th').length - 2;

	
	var splittedVal = newVal.split(',');
	var totalSum = 0;
    
    
    
	if(newVal.trim().length == 0){
		totalSum = 0;
	}else{
		splittedVal.forEach(function(elem){
			var values = elem.split('|');

			var isProviz = values[1] == "prov" ? true : false;
			var tempSum = Number(values[2]);
			var isEur = values[3] == "eur" ? true : false;
			var exchange = values[4];

			if(isEur){
				if(isProviz)
					totalSum += tempSum;
				else
					totalSum -= tempSum;
			} else {
				if(isProviz)
					totalSum += tempSum / exchange;
				else
					totalSum -= tempSum / exchange;
			}

		});
		totalSum = Math.round(totalSum * 1000) / 1000;
	}
	mainSumRow$.find('td:not(:first)').text( totalSum  + " Eur");
}

function getNumberFromStr(str){
	if(str){
		return Number(str)||0;
	}else if(typeof str == 'undefined' || str==""){
		return 0;
	}
}

function pretifyString(str){
	if(typeof str=="undefined") return;

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