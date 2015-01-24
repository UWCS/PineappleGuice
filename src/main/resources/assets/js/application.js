function alertEmptyQueue() {
    $("#notice").append("<p>Queue might be empty</p>");
}

function removeAlertEmptyQueue() {
    $("#notice").empty();
}

function queueYoutube(url) {
    $.ajax({
        url: "/api/queue/youtube?url=" + url
    }).done(function(response) {
        $("#submitYoutube").show();
        console.log("Successfully queued.");
    });

}

$(document).ready(function() {

    'use strict';

    var mediaList;

    $("#submitYoutube").click(function() {
        var url = $("#youtubeURL").val();
        $("#submitYoutube").hide();
        queueYoutube(url);
    });

    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload({
        url: 'api/queue/upload',
        add: function(e, data) {
            $(".filename").text(data.files[0].name);
            data.submit();
        },
        done: function() {
            $(".filename").text("");
            console.log("Done");
        }
    });

    setInterval(function() {
        $.ajax({
            url: "/api/queue",
            dataType: "json"
        }).done(function(data) {
            $("#Queue").empty();
            if (data.length == 0) {
                alertEmptyQueue();
            } else {
                removeAlertEmptyQueue();
                for (var i = 0; i < data.length; i++) {
                    if (data[i].queue.length == 0) continue;
                    var container = $("<div class=\"panel panel-primary\"></div>");

                    for (var j = 0; j < data[i].queue.length; j++) {
                        container.append("<div style=\"padding-left: 10px;\"><p>" + data[i].queue[j].name + "</div>");
                    }

                    $("#Queue").append(container);
                }
            }
        })
    }, 1000);
});