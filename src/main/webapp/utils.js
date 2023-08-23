

function compare(v1, v2) {
  // If non numeric value
  if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
    return v1.toString().localeCompare(v2); // lexical comparison
  }

  // If numeric value
  return v1 - v2; // v1 greater than v2 --> true
}

// TODO: move to other file
function buildTableRow(exam, editable = false) {
  let tr = $("<tr></tr>");
  // Add columns
  let td = $("<td></td>").text(exam.student.student_number);
  tr.append(td);
  td = $("<td></td>").text(exam.student.name);
  tr.append(td);
  td = $("<td></td>").text(exam.student.surname);
  tr.append(td);
  td = $("<td></td>").text(exam.student.email);
  tr.append(td);
  td = $("<td></td>").text(exam.student.school);
  tr.append(td);
  if (!editable) {
    td = $("<td></td>").text(getGradeDescription(exam.grade));
    tr.append(td);
  }
  else {
    td = $("<td></td>");

    // create input with number type
    let gradeInput = $("<input type='number' min='0' max='31' step='1'></input>");
    gradeInput.name = "grade";
    gradeInput.attr("data-id", exam.student.student_number);
    gradeInput.val(exam.grade)
    td.append(gradeInput);

    // add on edit listener, change class of input to modified and state to inserito
    gradeInput.on("input", function () {
      if (gradeInput.val() != exam.grade) {
        gradeInput.addClass("modified");
      }
      else {
        gradeInput.removeClass("modified");
      }
    });
    tr.append(td);
  }
  td = $("<td></td>").text(getDescriptionForResultState(exam.state));
  tr.append(td);
  return tr;
}

function getDescriptionForResultState(state) {
  switch (state) {
    case "NULL":
      return "Non inserito";
    case "INSERITO":
      return "Inserito";
    case "PUBBLICATO":
      return "Pubblicato";
    case "RIFIUTATO":
      return "Rifiutato";
    case "VERBALIZZATO":
      return "Verbalizzato";
    default:
      return "?";
  }
}

function getGradeDescription(grade) {
  if (grade == 31) {
    return "30 e lode";
  }
  else {
    return grade;
  }
}
