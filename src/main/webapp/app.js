{
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

    function ResultsList(_alert, _container, _body, _insertButton, _publishButton, _recordButton) {
        this.alert = _alert;
        this.container = _container;
        this.body = _body;
        this.insertButton = _insertButton;
        this.publishButton = _publishButton;
        this.recordButton = _recordButton;
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

            // if no results in state INSERITO or NULL, show record button
            let filteredList = retrievedList.filter((result) => result.state == "INSERITO" || result.state == "NULL");
            if (filteredList.length > 0) {
                this.recordButton.hide();
                this.publishButton.show();
                this.insertButton.show();
            }
            else {
                this.insertButton.hide();
                this.recordButton.show();
                this.publishButton.hide();
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
                    self.alert.text("Error while retrieving results: " + data.responseText);
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
                    self.alert.text("No results to insert");
                    self.alert.show();
                }
                else {
                    modalUpdate.show(filteredList, self.sessionId);
                }
            });
            this.publishButton.on("click", function () {
                self.action("publish");
            });
            this.recordButton.on("click", function () {
                self.action("record");
            });
        }

        this.action = function (action) {
            var self = this;
            $.ajax({
                url: apiUrl + "api/postResults",
                data: {
                    sessionId: this.sessionId,
                    action: action
                },
                type: "POST",
                success: function (data, state) {
                    self.refresh();
                },
                error: function (data, state) {
                    handle401or403(data);
                    self.alert.text("Error while " + action + "ing results: " + data.responseText);
                    self.alert.show();
                }
            });
        }

        this.refresh = function () {
            this.show(this.sessionId);
        }

        this.reset = function () {
            this.publishButton.hide();
            this.recordButton.hide();
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
        this.sessionId = null;

        // given table rows, add form form field for each row
        this.show = function (rows, sessionId) {
            this.sessionId = sessionId;
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
            var self = this;
            // create json array of modified only grade (by hidden input) with associated data-student number
            let modified = [];
            let inputs = this.tableBody.find("input.modified");
            for (let i = 0; i < inputs.length; i++) {
                let input = inputs[i];
                modified.push({
                    student_student_number: input.getAttribute("data-id"),
                    grade: input.value
                });
            }
            // send json array as json to server
            $.ajax({
                url: apiUrl + "api/editResults" + "?sessionId=" + this.sessionId,
                data: JSON.stringify(modified),
                type: "POST",
                contentType: "application/json",
                success: function (data, state) {
                    resultsList.refresh();
                    self.hide();
                },
                error: function (data, state) {
                    handle401or403(data);
                    self.alert.text("Error while updating results");
                    self.alert.show();
                }
            });
        }

        this.reset = function () {
            this.alert.hide();
            this.tableBody.empty();
            this.form.trigger("reset");
        }

        this.init = function () {
            var self = this;
            self.hide();
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
            resultsList = new ResultsList($("#alert"), $("#results-container"), $("#results-table-body"), $("#multiple-insert-btn"), $("#publish-btn"), $("#record-btn"), $("#action-form"));
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