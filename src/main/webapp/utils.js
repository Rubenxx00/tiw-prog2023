

function compare(v1, v2) {
  // If non numeric value
  if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
    return v1.toString().localeCompare(v2); // lexical comparison
  }

  // If numeric value
  return v1 - v2; // v1 greater than v2 --> true
}

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

    // create input with values from gradeMap and set it to the current grade
    let gradeInput = $("<select></select>");
    for (let key in gradeMap) {
      let option = $("<option></option>").attr("value", key).text(gradeMap[key]);
      if (key == exam.grade) {
        option.attr("selected", "selected");
      }
      gradeInput.attr("data-id", exam.student.student_number);
      gradeInput.append(option);
    }
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

const gradeMap = {
  0: "<vuoto>",
  1: "Assente",
  2: "Rimandato",
  3: "Riprovato",
  18: "18",
  19: "19",
  20: "20",
  21: "21",
  22: "22",
  23: "23",
  24: "24",
  25: "25",
  26: "26",
  27: "27",
  28: "28",
  29: "29",
  30: "30",
  31: "30 e Lode"
};


function getGradeDescription(grade) {
  return gradeMap[grade];
}

function getGradeFromDescription(description) {
  for (let key in gradeMap) {
    if (gradeMap[key] == description) {
      return key;
    }
  }
  return null;
}
