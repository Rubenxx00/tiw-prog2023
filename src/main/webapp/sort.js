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
    return v1 - v2;
  };
}

function sortTable(element) {
  var th = document.getElementById(element);
  var table = th.closest('table'); // get the closest table tag
  var rowHeaders = table.querySelectorAll('th');
 
  // Remove asc/desc classes from all headers
  rowHeaders.forEach(header => {
    header.classList.remove('asc', 'desc');
  });
  th.classList.add(asc ? 'asc' : 'desc');    

  var columnIdx = Array.from(rowHeaders).indexOf(th);
  var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));
  
  rowsArray.sort(createComparer(columnIdx, asc));

  asc =  !asc;
  for (var i = 0; i < rowsArray.length; i++) {
    table.querySelector('tbody').appendChild(rowsArray[i]);
  }
}
