const svgns = "http://www.w3.org/2000/svg";

const gridCellWidth = 25;
const convertToDays = 86400000;
const gantChartHeaderheightPlusMargin = 65;
const gridCellheight = 40;
const rowheight = 60;
const taskheight = 30;
const edgeWidth = 4;
const daysMargin = 5;
const yDays = 50;

document.addEventListener("DOMContentLoaded", loadGantt);

function loadGantt() {

    var dateGridStart = new Date($("#start_date").html());
    var dateGridEnd = new Date($("#finish_date").html());
    var width = (dateGridEnd.getTime()-dateGridStart.getTime()+ convertToDays)/convertToDays * gridCellWidth;
    
    $("#svg_gantt_area").attr("width", width.toString());
    $("#rows_header").attr("width", width.toString());

    var count = 0;

    var gTasks = $("#gantt_tasks").get(0);
    var gTasksProgress = $("#gantt_tasks_progress").get(0);
    var gRows = $("#gantt_rows").get(0);

    $(".table_striped tbody tr").each(function() {
        var currentRow=$(this);
        
        var date = new Date(currentRow.find("td:eq(2)").html());
        var x = ((date.getTime() - dateGridStart.getTime())/convertToDays)*gridCellWidth;
        var y = gantChartHeaderheightPlusMargin + gridCellheight * count; 
        var w = currentRow.find("td:eq(4)").html() * gridCellWidth;

        let newRow = document.createElementNS(svgns, "rect");
        newRow.setAttribute("x", "0");
        newRow.setAttribute("y", (rowheight + gridCellheight * count).toString());
        newRow.setAttribute("width", width);
        newRow.setAttribute("height", gridCellheight.toString());
        if (count % 2 == 0) {
            newRow.setAttribute("class", "grid_row_odd");
        } else {
            newRow.setAttribute("class", "grid_row_even");
        }
        gRows.appendChild(newRow);

        let newRect = document.createElementNS(svgns, "rect");
        newRect.setAttribute("x", x.toString());
        newRect.setAttribute("y", y.toString());
        newRect.setAttribute("width", w.toString());
        newRect.setAttribute("height", taskheight.toString());
        console.log(currentRow.find("td:eq(8)").html());
        if (currentRow.find("td:eq(8)").html() != "true") {
            newRect.setAttribute("class", "rect_task");
        } else {
            newRect.setAttribute("class", "rect_task_critical");
        }
        gTasks.appendChild(newRect);

        let newProg = document.createElementNS(svgns, "rect");
        newProg.setAttribute("x", x.toString());
        newProg.setAttribute("y", y.toString());
        newProg.setAttribute("width", (w * currentRow.find("td:eq(5)").html()/100).toString());
        newProg.setAttribute("height", taskheight.toString());
        newProg.setAttribute("class", "progress");
        gTasksProgress.appendChild(newProg);

        count++;
    })

    var height = rowheight + gridCellheight * count;
    $("#svg_gantt_area").attr("height", height.toString());
    var gLines = $("#lines").get(0);
    var gDays = $("#days").get(0);
    var gMonths = $("#months").get(0);
    var month = -1;


    for (var i=0; i < (dateGridEnd.getTime()-dateGridStart.getTime() + convertToDays)/convertToDays; i++) {
        var currentDate = new Date(dateGridStart.getTime() + i * convertToDays);
        let newDay = document.createElementNS(svgns, "text");
        newDay.setAttribute("x", (daysMargin + gridCellWidth * i).toString());
        newDay.setAttribute("y", yDays.toString());
        newDay.setAttribute("class", "grid_header_days");
        newDay.textContent = currentDate.getDate();
        gDays.appendChild(newDay);

        if (currentDate.getMonth() != month) {
            month = currentDate.getMonth();
            let newMonth = document.createElementNS(svgns, "text");
            newMonth.setAttribute("x", (daysMargin + gridCellWidth * i).toString());
            newMonth.setAttribute("y", "20");
            newMonth.setAttribute("class", "grid_header_months");
            switch(month) {
                case 0:
                    newMonth.textContent = "Январь " + currentDate.getFullYear().toString();
                    break;
                case 1:
                    newMonth.textContent = "Февраль  " + currentDate.getFullYear().toString();
                    break;
                case 2:
                    newMonth.textContent = "Март " + currentDate.getFullYear().toString();
                    break;
                case 3:
                    newMonth.textContent = "Апрель " + currentDate.getFullYear().toString();
                    break;
                case 4:
                    newMonth.textContent = "Май " + currentDate.getFullYear().toString();
                    break;
                case 5:
                    newMonth.textContent = "Июнь " + currentDate.getFullYear().toString();
                    break;
                case 6:
                    newMonth.textContent = "Июль  " + currentDate.getFullYear().toString();
                    break;
                case 7:
                    newMonth.textContent = "Август " + currentDate.getFullYear().toString();
                    break;
                case 8:
                    newMonth.textContent = "Сентябрь " + currentDate.getFullYear().toString();
                    break;
                case 9:
                    newMonth.textContent = "Октябрь " + currentDate.getFullYear().toString();
                    break;
                case 10:
                    newMonth.textContent = "Ноябрь " + currentDate.getFullYear().toString();
                    break;
                case 11:
                    newMonth.textContent = "Декабрь " + currentDate.getFullYear().toString();
                    break;
            }
            
            gMonths.appendChild(newMonth);
        }

        let newLine = document.createElementNS(svgns, "path");
        newLine.setAttribute("d", "M "+ (gridCellWidth * i).toString() + " 59 v "+ height.toString());
        newLine.setAttribute("class", "grid_line");
        gLines.appendChild(newLine);
    }
}

