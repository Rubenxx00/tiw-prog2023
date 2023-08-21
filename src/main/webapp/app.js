{
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
            td = $("<td></td>").text(exam.grade);
            tr.append(td);
        }
        else {
            td = $("<td></td>");
            let input = $("<input type='number' name='exam.student_student_number' min='0' max='31' step='1' required></input>");
            input.val(exam.grade);
            td.append(input);
            tr.append(td);
        }
        td = $("<td></td>").text(exam.state);
        tr.append(td);
        return tr;
      }
    const apiUrl = "http://localhost:8081/exam_war_exploded/";
    let courseList, sessionList, resultInfo, pageOrchestrator, resultsList, modalUpdate;
    let user;
    pageOrchestrator = new PageOrchestrator();

    $(document).ready(function () {
        if (!window.sessionStorage.getItem('user')) {
            window.location.href = "indexjs.html";
        }
        else {
            user = JSON.parse(window.sessionStorage.getItem('user'));
            pageOrchestrator.start();
            pageOrchestrator.refresh();
        }
    });

    function handle401or403(data) {
        if (data.status == 401 || data.status == 403) {
            window.sessionStorage.removeItem('login');
            window.sessionStorage.removeItem('role');
            window.location.href = data.getResponseHeader("Location");
        }
    }

    function CourseList(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.init = function () {
            this.listcontainer.hide();
        }

        this.show = function (next) {
            var self = this;
            $.ajax({
                url: apiUrl + "GetCoursesRIA",
                type: "GET",
                success: function (data, state) {
                    if (data.length == 0) {
                        self.alert.text("No courses found");
                        self.alert.show();
                        return;
                    }
                    self.update(data);
                    if (next)
                        next();
                },
                error: function (data, state) {
                    handle401or403(data);
                    self.alert.text("Error while retrieving courses");
                    self.alert.show();
                }
            });

        }

        this.update = function (retrievedList) {
            this.listcontainerbody.empty();
            for (let i = 0; i < retrievedList.length; i++) {
                let course = retrievedList[i];
                let tr = $("<tr></tr>");
                tr.on("click", function () {
                    sessionList.show(course.idcourse);
                    tr.addClass("selected");
                    tr.siblings().removeClass("selected");
                });
                let td = $("<td></td>").text(course.title);
                tr.append(td);
                td = $("<td></td>").text(course.teacher.name + " " + course.teacher.surname);
                tr.append(td);
                this.listcontainerbody.append(tr);
            }
            this.listcontainer.show();
        }

        this.autoselect = function () {
            this.listcontainerbody.find("tr:first").click();
        }
    }

    function SessionList(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.init = function () {
            this.listcontainer.hide();
        }

        this.show = function (courseId) {
            var self = this;
            $.ajax({
                url: apiUrl + "GetSessionRIA",
                data: { courseId: courseId },
                type: "GET",
                success: function (data, state) {
                    pageOrchestrator.refresh();
                    if (data.length == 0) {
                        self.alert.text("No sessions found");
                        self.alert.show();
                    }
                    self.update(data);
                },
                error: function (data, state) {
                    handle401or403(data);
                    self.alert.text("Error while retrieving sessions");
                    self.alert.show();
                }
            });
        }

        this.update = function (retrievedList) {
            this.listcontainerbody.empty();
            for (let i = 0; i < retrievedList.length; i++) {
                let session = retrievedList[i];
                let tr = $("<tr></tr>");
                tr.on("click", function () {
                    tr.addClass("selected");
                    tr.siblings().removeClass("selected");
                    if (user.role == "teacher") {
                        resultsList.show(session.idsession);
                    }
                    else {
                        resultInfo.show(session.idsession);
                    }
                });
                td = $("<td></td>").text(session.date);
                tr.append(td);
                this.listcontainerbody.append(tr);
            }
            this.listcontainer.show();
        }

        this.reset = function () {
            this.listcontainerbody.empty();
            this.listcontainer.hide();
        }
    }

    function ResultsList(_alert, _container, _body, _insertButton) {
        this.alert = _alert;
        this.container = _container;
        this.body = _body;
        this.insertButton = _insertButton;
        this.resultList = [];

        this.update = function (retrievedList) {
            this.resultList = retrievedList;
            this.reset();
            if (retrievedList.length == 0) {
                let tr = $("<tr></tr>");
                let td = $("<td></td>").text("No results found");
                tr.append(td);
                this.body.append(tr);
            }

            for (let i = 0; i < retrievedList.length; i++) {
                let exam = retrievedList[i];
                let tr = buildTableRow(exam);
                this.body.append(tr);
            }

            this.container.show();
        }

        this.show = function (sessionId) {
            var self = this;
            this.sessionId = sessionId;
            $.ajax({
                url: apiUrl + "GetResultsRIA",
                data: { sessionId: sessionId },
                type: "GET",
                success: function (data, state) {
                    pageOrchestrator.refresh();
                    self.update(data);
                },
                error: function (data, state) {
                    handle401or403(data);
                    self.alert.text("Error while retrieving results");
                    self.alert.show();
                }
            });
        }

        this.init = function () {
            let self = this;
            this.container.hide();
            // TODO: better sorting
            $('.sortable').click(function () {
                sortTable($(this).attr('id'));
            });
            this.insertButton.on("click", function () {
                // filter retrievedList to get only result with INSERITO or NULL state
                let filteredList = self.resultList.filter((result) => result.state == "INSERITO" || result.state == "NULL");
                if (filteredList.length == 0) {
                    alert("No results to insert");
                }
                else {
                    modalUpdate.show(filteredList);
                }
            });
                
        }

        this.refresh = function () {
            this.show(this.sessionId);
        }

        this.reset = function () {
            this.container.hide();
            this.body.empty();
        }
    }

    // vertical table with student info
    function ResultInfo(_alert, _container, _button) {
        this.alert = _alert;
        this.container = _container;
        this.button = _button;

        this.init = function () {
            this.container.hide();
            this.button.on("click", function () {
                // TODO
            }
            );
        }

        this.show = function (sessionId) {
            var self = this;
            $.ajax({
                url: apiUrl + "GetStudentResultRIA",
                data: { sessionId: sessionId },
                type: "GET",
                success: function (data, state) {
                    pageOrchestrator.refresh();
                    self.update(data);
                },
                error: function (data, state) {
                    handle401or403(data);
                    self.alert.text("Error while retrieving results");
                    self.alert.show();
                }
            });
        }

        this.update = function (result) {
            if (result.state == "INSERITO") {
                this.button.show();
            }
            var tds = this.container.find('table.table td');

            tds.eq(0).text(result.student.student_number);
            tds.eq(1).text(result.student.name);
            tds.eq(2).text(result.student.surname);
            tds.eq(3).text(result.student.email);
            tds.eq(4).text(result.student.school);
            tds.eq(5).text(result.state);
            tds.eq(6).text(result.grade);
            this.container.show();

        }

        this.reset = function () {
            this.container.hide();
        }
    }

    function ModalUpdate(_alert, _container, _tableBody, _form, _cancelButton, _submitButton) {
        this.alert = _alert;
        this.modal = _container;
        this.tableBody = _tableBody;
        this.form = _form;
        this.cancelButton = _cancelButton;
        this.submitButton = _submitButton;

        // given table rows, add form form field for each row
        this.show = function (rows) {
            var self = this;
            this.reset();
            for (let i = 0; i < rows.length; i++) {
                let row = rows[i];
                let tr = buildTableRow(row, true);
                this.tableBody.append(tr);
            }
            this.modal.show();
        }

        this.hide = function () {
            this.modal.hide();
        }

        this.submit = function () {
            // TODO
            // log form data
            var formData = this.form.serialize();
            console.log(formData);
            this.hide();
        }

        this.reset = function () {
            this.alert.hide();
            this.tableBody.empty();
            this.form.trigger("reset");
        }

        this.init = function (){
            var self = this;
            this.modal.hide();
            this.cancelButton.on("click", function () {
                self.hide();
            });
            this.form.submit(function (event) {
                event.preventDefault();
                self.submit();
            });
        }

    }


    function PageOrchestrator() {
        var alert = $("#alert");
        this.start = function () {
            $("#username").text(user.name + " " + user.surname);
            $("#role").text(user.role);

            courseList = new CourseList($("#alert"), $("#course-table"), $("#course-table-body"));
            sessionList = new SessionList($("#alert"), $("#session-table"), $("#session-table-body"));
            resultsList = new ResultsList($("#alert"), $("#results-container"), $("#results-table-body"), $("#multiple-insert-btn"));
            resultInfo = new ResultInfo($("#alert"), $("#resultInfo-container"), $("#reject-btn"));
            modalUpdate = new ModalUpdate($("#modal-alert"), $("#modal-container"), $("#modal-table-body"), $("#modal-form"), $("#modal-cancel-btn"), $("#modal-submit-btn"));
            courseList.init();
            sessionList.init();
            resultsList.init();
            resultInfo.init();
            modalUpdate.init();

            courseList.show(
                function () { courseList.autoselect(); }
            );
        }

        this.refresh = function () {
            resultsList.reset();
            resultInfo.reset();
            alert.text("");
            alert.hide();
        }

    }
}