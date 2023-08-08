{
    const apiUrl = "http://localhost:8081/exam_war_exploded/";
    let courseList, sessionList, pageOrchestrator;

    pageOrchestrator = new PageOrchestrator();

    $(document).ready(function () {
        if (!window.sessionStorage.getItem('login')) {
            window.location.href = "indexjs.html";
        }
        else {
            pageOrchestrator.start();
            pageOrchestrator.refresh();
        }
    });

    function handle401or403(data) {
        if (data.status == 401 || data.status == 403) {
            window.sessionStorage.removeItem('login');
            window.sessionStorage.removeItem('role');
            window.location.href = data.header("Location");
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
                    if (data.length == 0) {
                        self.alert.text("No sessions found");
                        self.alert.show();
                        return;
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
                    resultList.body.empty();
                    resultList.show(session.idsession);
                });
                td = $("<td></td>").text(session.date);
                tr.append(td);
                this.listcontainerbody.append(tr);
            }
            this.listcontainer.show();
        }

    }

    function ResultsList(_alert, _container, _body) {
        this.alert = _alert;
        this.container = _container;
        this.body = _body;

        this.update = function (retrievedList) {
            this.reset();
            if (retrievedList.length == 0) {
                let tr = $("<tr></tr>");
                let td = $("<td></td>").text("No results found");
                tr.append(td);
                this.body.append(tr);
                return;
            }
            for (let i = 0; i < retrievedList.length; i++) {
                let exam = retrievedList[i];
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
                td = $("<td></td>").text(exam.grade);
                tr.append(td);
                td = $("<td></td>").text(exam.state);
                tr.append(td);

                this.body.append(tr);
            }
            this.container.show();
        }

        this.show = function (sessionId) {
            var self = this;
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
            this.container.hide();
        }

        this.reset = function () {
            this.body.empty();
        }
    }

    function PageOrchestrator() {
        var alert = $("#alert");
        this.start = function () {
            courseList = new CourseList($("#alert"), $("#course-table"), $("#course-table-body"));
            sessionList = new SessionList($("#alert"), $("#session-table"), $("#session-table-body"));
            resultList = new ResultsList($("#alert"), $("#results-container"), $("#results-table-body"));
            courseList.init();
            sessionList.init();
            resultList.init();

            courseList.show(
                function () { courseList.autoselect(); }
            );
        }

        this.refresh = function () {
            alert.text("");
            alert.hide();
        }
    }
}