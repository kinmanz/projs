<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html lang="en">
    <head>
        <title>W6 analysis</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
        <script src="//code.jquery.com/jquery-1.10.2.js"></script>
        <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>

    </head>

    <body >
            <div class="container-fluid">
                <div class="row">
                    <div class="col-sm-12 header" id="header">
                        <h1 id="logo">
                            <img id="logo" class="logo" src="logo/Logo/W6.png"/>
                        </h1>
                        <h2 id="site-name">W6 assess</h2>
                        <h4 id="site-slogan">news based security profiling</h4>
                    </div>
                    <div>
                        <div class="row">
                            <div class="col-sm-12">
                                <div class="form-group">

                                    <script>
                                        function addCell(cell, text) {
                                            if (text != null)
                                                cell.innerHTML =  
                                                    "<label style = \"font-weight: normal;\">" + text + "</label>"
                                                    ;
                                        }
                                        window.onload = function () {
                                            var i, output;
                                            var tableElement = document.getElementById("myTable");
                                            var json = ${response}; 
                                            for (i = 0; i < json.length; i++)
                                            {
                                                var table = JSON.parse(json[i]["response"]).table;
                                                console.log(table);

                                                var who = table.who;
                                                var what = table.what;
                                                var weapon = table.weapon;
                                                var whom = table.whom;
                                                var when = table.when;
                                                var where = table.where;
                                                console.log(who);
                                                var tableLength = Math.max(who.length, what.length, weapon.length, whom.length, when.length, where.length, 1);
                                                for (j = 0; j < tableLength; ++j)
                                                {
                                                    var x = document.getElementById("myTable").rows.length;
                                                    var row = tableElement.insertRow(x);
                                                    var info = row.insertCell(0);
                                                    var title = row.insertCell(1);
                                                    if (j == 0)
                                                    {
                                                        info.innerHTML =  "<a href=\"parse?id=" + json[i].id +"\">" + json[i].title + "</a>";
                                                        addCell(title, json[i].sourse);
                                                    }
                                                    addCell(row.insertCell(2), who[j]);
                                                    addCell(row.insertCell(3), weapon[j]);
                                                    addCell(row.insertCell(4), what[j]);
                                                    addCell(row.insertCell(5), whom[j]);
                                                    addCell(row.insertCell(6), where[j]);
                                                    addCell(row.insertCell(7), when[j]);                                                        
                                                }
                                            }
                                        }
                                    </script>

                                </div>
                            </div>
                            <div class="col-sm-12 " style="background:lightgray" >
                                <table id="myTable" class="table" style = "overflow:scroll">
                                    <thead>
                                        <tr>
                                            <th >Title</th>
                                            <th >Source</th>
                                            <th ><span class="fa fa-circle who headertable" aria-hidden="true"></span> Who</th>
                                            <th ><span class="fa fa-circle weapon headertable" aria-hidden="true"></span> Weapon</th>
                                            <th ><span class="fa fa-circle what headertable" aria-hidden="true"></span> What</th>
                                            <th ><span class="fa fa-circle whom headertable" aria-hidden="true"></span> Whom</th>
                                            <th ><span class="fa fa-circle where headertable" aria-hidden="true"></span> Where</th>
                                            <th ><span class="fa fa-circle when headertable" aria-hidden="true"></span> When</th>

                                        </tr>
                                    </thead>
                                </table>


                            </div>
                        </div>
                    </div>
                </div>
            </div>
    </body>
</html>
