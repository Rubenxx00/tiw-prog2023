

function compare(v1, v2) {
  // If non numeric value
  if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
    return v1.toString().localeCompare(v2); // lexical comparison
  }

  // If numeric value
  return v1 - v2; // v1 greater than v2 --> true
}

