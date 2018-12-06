
function generateAndroidSdk(app,lang) {
    $("#cssload-contain").fadeIn();
    jagg.post("/site/blocks/subscription/subscription-list/ajax/subscription-list.jag?action=generateSdk&selectedApp=&language=", {
        action:"generateSdk",
        selectedApp:app,
        language:lang,
    }, function (result) {
        $("#cssload-contain").fadeOut();
        if (result.error) {
            jagg.message({content:result.message,type:"error"});
        }
    }, "json");
}



function triggerOperatorSubscribe(deploymentType) {
    $.ajaxSetup({
        contentType: "application/x-www-form-urlencoded; charset=utf-8"
    });

    var application = $("#application-name").val();
    var operators = $("#operator-list").val();
    var operatorList, operatorStr = "";
    var tier = $("#tiers-list").val();
    var callbackUrl = $("#callback-url").val();
    var description = $("#app-description").val();
    var status='';
    var applicationId = $("#application-id").val();

    if (deploymentType == "hub") {
        if (operators == null || operators.length == 0) {
            jagg.message({
                content: i18n.t('Please select atleast one operator'),
                type: "info"
            });
            return;
        }

        else {

            // Persist the selected list of operators.


            for (var i = 0; i < operators.length; i++) {
                if (operatorStr.length > 0) {
                    operatorStr = operatorStr + "," + operators[i];
                } else {
                    operatorStr = operators[i];
                }
            }

            jagg.post("/site/blocks/application/application-add-operator/ajax/application-add-operator.jag", {
                action:"addApplication",
                application:application,
                applicationId:applicationId,
                tier:tier,
                callbackUrl:callbackUrl,
                description:description,
                deploymentType:deploymentType,
                operatorList: operatorStr
            }, function (result) {
                if (result.error == false) {
                    status=result.status;
                    jagg.message({content:i18n.t("Application approval request delivered to new operators."),type:"info",
                        cbk:function() {
                            window.location =  jagg.url("/site/pages/applications.jag");
                        }
                    });
                } else {
                    jagg.message({content:result.message,type:"error"});
                }
            }, "json");

        }
    }
}