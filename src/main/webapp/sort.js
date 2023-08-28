// Returns the text content of a cell.
var asc = true;

function getCellValue(tr, idx) {
  return tr.children[idx].textContent; // idx indexes the columns of the tr row
}

/*
* Creates a function that compares two rows based on the cell in the idx
* position.
*/
function createComparer(idx, asc) {
  return function(rowa, rowb) {
    // get values to compare at column idx
    // if order is ascending, compare 1st row to 2nd , otherwise 2nd to 1st
    var v1 = getCellValue(asc ? rowa : rowb, idx),
    v2 = getCellValue(asc ? rowb : rowa, idx);
    if (idx === 5) {
      v1 = getGradeFromDescription(v1);
      v2 = getGradeFromDescription(v2);
    }    
    // If non numeric value
    if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
      return v1.toString().localeCompare(v2); // lexical comparison
    }

    // If numeric value
    return v1 - v2; // v1 greater than v2 --> true
  };
}

// For all table headers f class sortable
function sortTable(element) {
  var th = document.getElementById(element);
  var table = th.closest('table'); // get the closest table tag
  var rowHeaders = table.querySelectorAll('th');
  var columnIdx =  Array.from(rowHeaders).indexOf(th);
  
  // For every row in the table body
  // Use Array.from to build an array from table.querySelectorAll result
  // which is an Array Like Object (see DOM specifications)
  var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));
  
  // sort rows with the comparator function passing
  // index of column to compare, sort criterion asc or desc)
  rowsArray.sort(createComparer(columnIdx, asc));
  
  //  Toggle the criterion
  asc =  !asc;
  // Append the sorted rows in the table body
  for (var i = 0; i < rowsArray.length; i++) {
    table.querySelector('tbody').appendChild(rowsArray[i]);
    // https://developer.mozilla.org/en-US/docs/Web/API/Node/appendChild
  }
  //rowsArray.forEach(function(row){table.querySelector('tbody').appendChild(row);});
}