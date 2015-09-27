<!DOCTYPE html>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic" %>

<%@ page import="com.isomorphic.base.Config" %>
<%@ page import="com.isomorphic.js.JSTranslater" %>
<%@ page import="com.isomorphic.datasource.DataSource" %>
<%@ page import="com.isomorphic.datasource.DataSourceManager" %>

<HTML><HEAD>
<STYLE>
.normal         {font-family:Verdana; font-size:12px;}
.pageHeader2    {font-family:Verdana; font-size:24px; font-weight:bold;}
</STYLE>
<TITLE>Result Viewer</TITLE>

</HEAD><BODY BGCOLOR='#DDDDDD' CLASS=normal style="overflow: hidden">

<!-- load Isomorphic SmartClient -->
<isomorphic:loadISC modulesDir="system/development/"
                    skin="Enterprise"
                    includeModules="RichTextEditor, FileLoader" />

<%
Config myConfig  = Config.getGlobal();
String batchRunDS   = myConfig.getString("autotest.batchRunDS");
String testResultDS = myConfig.getString("autotest.testResultDS");
String testUploadDS = myConfig.getString("autotest.testUploadDS");

if (batchRunDS   == null) batchRunDS   = "batchRun";
if (testResultDS == null) testResultDS = "testResult";
if (testUploadDS == null) testUploadDS = "testUpload";
%>

<SCRIPT>

// inject DataSources specified by the Config object into JS
var batchRunDS   = <isomorphic:loadDS name="<%= batchRunDS %>"/>;
var testResultDS = <isomorphic:loadDS name="<%= testResultDS %>"/>;
var testUploadDS = <isomorphic:loadDS name="<%= testUploadDS %>"/>

function logContainsHTML()    { return batchRunDS.fields["modifiedFiles"] != null; }
function hasLastPassDate()    { return testResultDS.fields["lastPass"]    != null; }
function hasResultHistory()   { return testResultDS.fields["history"]     != null; }
function supportsTestNumber() { return testResultDS.fields["testNumber" ] != null; }
function descriptionPresent() { return testResultDS.fields["description"] != null; }

// open link to specific batch referenced in a test's result history
function jumpToTestResult (fileGridRowNum, batchGridRowOffset) {
    var fileGrid    = resultViewer.fileGrid,
        batchesGrid = resultViewer.batchesGrid;

    var currentBatchRowNum = batchesGrid.getRecordIndex(batchesGrid.getSelection()[0]);
    if (currentBatchRowNum < 0) return;

    var targetBatch = batchesGrid.getRecord(currentBatchRowNum + batchGridRowOffset);
    if (targetBatch == null) return;

    var record = fileGrid.getRecord(fileGridRowNum);
    if (record == null) return;

    window.open(fileGrid.createURL(record.branch, targetBatch.batchStartTime,
                                   record.showcase, record.testFile));
};

// capability to launch into a view specified via URL parameters
function getURLParameter (name) {
    var match = location.href.match(new RegExp("[?&](?:" + name + ")=([^&#]*)"));
    return match && match.length > 1 ? match[1] : null;
};

isc.setAutoDraw(false);

// RichTextLayout provides special handling for HTML-based log mesasges
isc.defineClass("RichTextLayout", "VLayout").addProperties({

    members: [isc.RichTextCanvas.create({
        name: "htmlContent",
        editable: false,
        overflow: "auto"
    })],
    
    setLogMessage : function (value) {
    
        // wrap plain text in protective <pre> tags
        if (isc.isA.String(value) && !value.match(/^\s*<html>/i)) {
            value = "<pre>" + value + "</pre>";
        }
        var html = this.getMember("htmlContent");
        html.setContents(value || "");
    }
});

// FileUploadDialog simply wraps FileUploadForm
isc.defineClass("FileUploadDialog", "Dialog").addProperties({

    title: "Upload Selenium RC Test File to Server",

    autoDraw: true,
    isModal: true,
    autoSize:  true,
    autoCenter: true,
    showModalMask: true,
    canDragReposition: true,

    bodyProperties: {
            
        fileUploadFormDefaults: {
    
            _constructor: DynamicForm,
            
            numCols: 5,
            colWidths: [80, 40, 80, 40, 80],
            autoDraw: false,
        
            dataSource: testUploadDS,
            
            items: [
                // choose the file to be uploaded
                {
                    name: "file",
                    title: "Test File to Upload",
                    wrapTitle: false,
                    height: 40,
                    width: 250,
                    colSpan: 4
                },
                // choose the server path for upload
                {
                    name: "serverPath",
                    title: "Server Path",
                    titleAlign: "left",
                    type: "text",
                    width: 250,
                    colSpan: 4
                }
            ],
        
            addButtonItem : function (properties, addSpacer) {
                this.addItem(isc.addProperties(properties, {
                    name: properties.title.toLowerCase(),
                    editorType: "ButtonItem",
                    validateOnExit: true,
                    vAlign: "center",
                    startRow: false,
                    endRow: false,
                    width: 60,
                    colSpan: 1
                }));
                if (addSpacer) this.addItem({
                    editorType: "StaticTextItem", showTitle: false,
                    shouldSaveValue: false,
                    height: 40
                });
            },
            
            initWidget : function () {
                this.Super("initWidget", arguments);
                
                this.file = this.getItem("file");
                this.path = this.getItem("serverPath");
                // add cancel, clear, upload buttons
                this.addButtonItem({
                    title: "Cancel",
                    click : function(form) { 
                        form.dialog.closeClick();
                        isc.logWarn(form.creator.ID);
                    }
                }, true);
                this.addButtonItem({
                    title: "Clear",
                    click : function(form) { 
                        form.file.clearValue(); 
                        form.path.clearValue();
                    }
                }, true);
                this.addButtonItem({
                    title: "Upload",
                    click : function(form) { 
                        form.saveData(form.callback, {callingForm: form});
                    }
                });
            },
        
            callback : function (dsResponse, data, dsRequest) {
                var status = dsResponse.status;
                if (status >= 0) {
                    var form = dsRequest.callingForm;
                    form.dialog.closeClick();
                    isc.say("Saved Successfully");
                }
                else isc.warn("Error " + status + " received while saving");
            }
        },
        initWidget : function () {
            this.Super("initWidget", arguments);
            this.addAutoChild("fileUploadForm");
            this.fileUploadForm.dialog = this.creator;
        }
    },
    
    initWidget : function () {
        this.Super("initWidget", arguments);
        //var form = isc.FileUploadForm.create({dataSource: testUploadDS});
        //form.dialog = this;
        //this.addAutoChild("layout");
        //this.addAutoChild("fileUploadForm");
        //this.fileUploadForm.dialog = this;
            
        //this.addItem(form);
    }
});
   
var CONSTANTS = {
    margin: 5
};

// ResultViewer is the main class containing all sections
isc.defineClass("ResultViewer", isc.VLayout).addProperties({

    // BatchesGrid shows all the batches for the selected branch
    batchesGridDefaults: {
        _constructor: isc.ListGrid,
        dataSource: batchRunDS,
        layoutBottomMargin: CONSTANTS.margin,
        
        sortField: "batchStartTime",
        sortDirection: "descending",
    
        canHover: true,
        canDragResize: true,
        autoFitFieldWidths: true,
        autoFitWidthApproach: "both",
        autoFitClipFields: [],
    
        autoFetchData: false,
        showFilterEditor: true,
        datetimeFormatter: "toSerializeableDate",
    
        fields: [{name: "id"},
                 {name: "batchStartTime"},
                 {name: "batchEndTime"},
                 {name: "user"}],
    
        initWidget : function () {
            this.targetBatch = getURLParameter("batch");
            var fieldName = logContainsHTML() ? "modifiedFiles" : "log";
            this.fields.add({name: fieldName});
            this.autoFitClipFields.add(fieldName);
            this.autoFitExpandField = fieldName;
            this.Super("initWidget", arguments);
        },
    
        // selection changes trigger recomputation of fixes/regression statistics
        updateFirstRecord : function (selection) {
            if (selection.length < 1 && this.firstRecord != null) {
                var viewer = this.creator;
                this.firstRecord = null;
                viewer.logArea.setLogMessage();
                viewer.fileGrid.setData([]);
                return true;
            }
            if (selection.length > 0 && selection[0] != this.firstRecord) {
                var record = this.firstRecord = selection[0],
                    viewer = this.creator;
                viewer.logArea.setLogMessage(record.log);
                viewer.fileGrid.loadBatch(record.batchStartTime);
                return true;
            }
            return false;
        },
        updateSecondRecord : function (selection) {
            if (selection.length < 2 && this.secondRecord != null) {
                this.secondRecord = null;
                return true;
            }
            if (selection.length > 1 && selection[1] != this.secondRecord) {
                this.secondRecord = selection[1];
                return true;
            }
            return false;
        },
        selectionChanged : function (record, state) {
            var selection = this.getSelection() || [];
    
            // countermand attempt to select more than 2 records
            if (selection.length > 2 && state) {
                this.deselectRecord(record);
            }
            var firstUpdated  = this.updateFirstRecord(selection),
                secondUpdated = this.updateSecondRecord(selection);
    
            // update the statistics to deal with the changed batch selection
            if (firstUpdated || secondUpdated) {
    
                var time1 = this.firstRecord  ? this.firstRecord.batchStartTime  : null,
                    time2 = this.secondRecord ? this.secondRecord.batchStartTime : null;
    
                if (time1 != null) {
                    if (time2 == null) {
                        var index = this.getRecordIndex(this.firstRecord),
                            predecessor = index >= 0 ? this.getRecord(index + 1) : null;
                        if (predecessor != null) time2 = predecessor.batchStartTime;
                    }
                    var record = isc.shallowClone(this.firstRecord),
                        viewer = this.creator;
                    viewer.fetchFileResults(time1, time2 ? time2 : time1, 
                                            record, viewer.statsGrid);
                }
            }
        },
    
    
        getCellCSSText: function (record, rowNum, colNum) {
            if (!hasResultHistory() || record == Array.LOADING || record.history != null) {
                return this.baseStyle;
            }
            return this.isSelected(record) ? "background-color:#ff8800" :
                                             "background-color:#ffcc00";
        },
    
        // select the batch requested by user via URL parameters
        findTargetBatch : function (targetRow) {
    
            if (!isc.AutoTest.isGridDone(this) || 
                targetRow && Array.isLoading(this.getRecord(targetRow)))
            {
                return this.delayCall("findTargetBatch", [targetRow], 500);
            }
    
            var grid = this,
                batchStartTime = new Date(parseInt(this.targetBatch)),
                record = this.find(isc.DataSource.convertCriteria({
                    batchStartTime: batchStartTime
                }));
    
            // if the row has already been loaded, go to it now and select it
            if (record != null) {
                var index = this.getRecordIndex(record);
                this.scrollToRow(index);
                this.selectRecord(index);
                return;
            }
    
            // don't keep fetching in the event of an error
            if (targetRow != null) return;
    
            var grid = this,
                dataSource = this.dataSource,
                branch = this.creator.getBranch();
    
            // since target row not found find it and scroll to it
            dataSource.performCustomOperation("getTargetBatchOffset", {
                branch: branch, batchStartTime: batchStartTime
            }, function(dsResponse, data, dsRequest) {
                if (isc.isAn.Array(data)) data = data[0];
                if (isc.isAn.Object(data)) {
                    grid.scrollToRow(data.offset);
                    grid.delayCall("findTargetBatch", [data.offset], 500);
                }
            });
        },
    
        // install initial per-branch view
        load : function (branch) {
            var grid = this,
                viewer = this.creator;
            this.firstRecord = null;
            this.secondRecord = null;
            this.fetchData({branch: branch}, this.targetBatch != null ? 
                          function () { grid.findTargetBatch(); } : null);
            viewer.logArea.setLogMessage();
            viewer.statsGrid.setData([]);
            viewer.fileGrid.setData([]);
        }
    },
    
    // BatchStatsGrid shows the fixes/regressions for selected pair of batches
    statsGridDefaults: {
        _constructor: isc.ListGrid,
        autoFitData: "vertical", 
        dataSource: batchRunDS,
        layoutBottomMargin: CONSTANTS.margin,
        showEmptyMessage: false,
        //size based on default headerHeight + cellHeight + border/padding
        height: 47,
        leaveScrollbarGap: false,
    
        autoFitFieldWidths: true,
        autoFitWidthApproach: "both",
    
        autoFetchData: false,
        datetimeFormatter: "toSerializeableDate",
    
        fields: [
            // fields from DataSource
            {name: "id"},
            {name: "user"},
            // derived stats fields
            {name: "nBatchDuration", title: "Batch Duration (MM:SS)"}, 
            {name: "nPassedFiles", title: "Passed Files" },
            {name: "nTotalFiles",  title: "Total Files"  },
            {name: "nFixes",       title: "Fixes"        },
            {name: "nRegressions", title: "Regressions"  }
        ],
        _testFields: [
            {name: "nPassedTests", title: "Passed Tests"},
            {name: "nTotalTests",  title: "Total Tests"}
        ],
    
        hilites:
        [{fieldName: "nPassedTests", criteria: {}, textColor: "green"},
         {fieldName: "nPassedFiles", criteria: {}, textColor: "green"},
         {fieldName: "nFixes",       criteria: {}, textColor: "green"},
         {fieldName: "nRegressions", criteria: {}, textColor: "red"  }],
    
        initWidget : function () {
            if (testResultDS.fields["testNumber"] != null) {
                this.fields.addListAt(this._testFields, 4);
            }
            this.Super("initWidget", arguments);
        },
    
        installStats : function (record) {
            var duration = record.batchEndTime - record.batchStartTime;
            //if complete, calculate duration
            if (! isNaN(duration)) {
                var seconds = duration/1000;
                var mm = Math.round(seconds/60);
                var ss = Math.round(seconds%60);
                record.nBatchDuration = mm + ":" + ss ;
            }
            this.setData([record]);
        }
    },

    // LogArea displays the HTML content of the log from the selected batch    
    logAreaDefaults: {
        _constructor: isc.RichTextLayout,
        layoutBottomMargin: CONSTANTS.margin
    },
    
    // TestFileGrid shows all the files associated with first selected batch
    fileGridDefaults: {
        _constructor: isc.ListGrid,
        dataSource: testResultDS,
        layoutBottomMargin: CONSTANTS.margin,
        
        autoFitFieldWidths: true,
        autoFitWidthApproach: "both",
        autoFitClipFields: ["description", "messages", "serverLogs"],
    
        canDragResize: true,
        autoFetchData: false,
        showFilterEditor: true,
        enforceVClipping: true,
        datetimeFormatter: "toSerializeableDate",
    
        useAllDataSourceFields: true,
        fields: [
            {name: "batchId",        showIf: "false"},
            {name: "branch",         showIf: "false"},
            {name: "batchStartTime", showIf: "false"},
            {name: "startTime",      showIf: "false"},
            {name: "endTime",        showIf: "false"},
            {name: "details",        showIf: "false"},
            {name: "capture",        showIf: "false"},
            {name: "result",         multiple: true },
            {name: "showcase",       filterEditorProperties: {
                // records created by the original .test framework are represented by null
                changed: function(form, item, value) {
                    if (value === ".testframework") {
                        item.operator = "isNull";
                    } else {
                        item.operator = form.defaultSearchOperator;
                    }
                }
            }},
            {
                name: "testFile",
                editorType: "comboBox",
                optionDataSource: testResultDS,
                optionFilterContext: {groupBy: "testFile"},
                filterEditorProperties: {operator: "equals"}
            }
        ],

        cutoffs: {
            ISC_83_BRANCH:  Date.parseInput("2012-10-12 01:01:01","YMD"),
            ISC_90_BRANCH:  Date.parseInput("2013-06-09 01:01:01","YMD"),
            ISC_91_BRANCH:  Date.parseInput("2014-02-20 02:47:43","YMD"),
            ISC_100_BRANCH: Date.parseInput("2014-08-16 13:42:44","YMD")
        },
    
        resultLinkColor: {
            P: "green",
            F: "red",
            O: "red"
        },
    
        createURL : function (branch, batch, showcase, testFile) {
            var cutoff = this.cutoffs[branch];
            if (cutoff && batch < cutoff) branch = "MAIN";
    
            return location.href.replace(/[?&].*$/, "") + "?branch=" + branch + 
                "&batch=" + Date.parseInput(batch,"YMD").getTime() + 
                "&showcase=" + (showcase != null ? showcase : "dottest") + 
                "&testFile=" + encodeURIComponent(testFile);
        },
    
        transformResultHistory : function (record, rowNum) {
            var history = record.history;
            if (history == null) return null;
    
            var html = "<div style='font-family:monospace'>";
            for (var i = 0; i < history.length; i++) {
                var click = "",
                    result = history[i],
                    color = this.resultLinkColor[result] || "black";
                if (color != "black") {
                    click = " onclick='jumpToTestResult(" + rowNum + "," + (i + 1) + ")'";
                } 
                html += "<a style='color:" + color + "'" + click + ">" + result + "</a>";
            }
            return html + "</div>";
        },
    
        initWidget : function () {
            this.targetShowcase = getURLParameter("showcase");
            this.targetHidePass = getURLParameter("hidePass");
    
            var testFile = getURLParameter("testFile");
            if (testFile != null) this.targetTestFile = decodeURIComponent(testFile);
    
            // show "never succeeded" if there's no record of any passing batch
            if (hasLastPassDate()) this.fields.add({
                name: "lastPass", align: "left", showHover: true, 
                formatCellValue : function (value, record, rowNum, colNum, grid) {
                    return value != null || record.history == null ? value : "Never Succeeded";}
            });
            // transform raw test result history into clickable colored links
            if (hasResultHistory()) this.fields.add({name: "history",
                formatCellValue : function (value, record, rowNum, colNum, grid) {
                    return grid.transformResultHistory(record, rowNum);
            }});
            // customer records don't contain description, but can have server logs
            if (descriptionPresent()) {
                this.autoFitExpandField = "description";
                this.fields.add({name: "serverLogs", showIf: "false"});
            } else {
                this.autoFitExpandField = "messages";
            }
        
            this.Super("initWidget", arguments);

        },
    
        hasValidLastPass : function (field, record) {
            return field.name == "lastPass" && record.lastPass != null && 
                Date.compareDates(record.lastPass, record.batchStartTime) != 0;
        },
    
        recordClick : function (viewer, record, recordNum, field, fieldNum) {
            var grid = this;
    
            if (!this.hasValidLastPass(field, record)) return;

            var open = function (dsResponse, data) {
                if (isc.isAn.Array(data) && data.length > 0) {
                    var targetBatchStartTime = data[0].batchStartTime;
                    window.open(grid.createURL(record.branch,   targetBatchStartTime, 
                                               record.showcase, record.testFile));
                }
            };
            var advancedCriteria = { _constructor: "AdvancedCriteria", operator: "and", criteria: [
                {fieldName: "branch",         operator: "equals",      value: record.branch},
                {fieldName: "showcase",       operator: "equals",      value: record.showcase},
                {fieldName: "testFile",       operator: "equals",      value: record.testFile},
                {fieldName: "batchStartTime", operator: "greaterThan", value: record.lastPass}
            ]};
            testResultDS.fetchData(advancedCriteria, open, {
                sortBy: "batchStartTime", startRow: 0, endRow: 1
            });
        },
    
        cellHoverHTML : function (record, rowNum, colNum) {
            if (this.hasValidLastPass(this.getField(colNum), record)) {
                return "click to jump to first failure after last success";
            }
        },
    
        contextMenu: {
            data:[{
                title:"View History of Test", 
                click: function(target, item, menu) {
                    target.openHistoryInWindow(target);
                }
            }]
        },

        openHistoryInWindow : function(sourceGrid) {

            var record = sourceGrid.getSelectedRecord();
            var section = this.creator.stack.sectionForItem(this);

            var initialCriteria = { 
                _constructor: "AdvancedCriteria", 
                operator: "and", 
                criteria: [
                    {fieldName: "branch", operator: "iEquals", value: record.branch},
                    {fieldName: "testFile", operator: "iEquals", value: record.testFile},
                    {fieldName: "testNumber", operator: "equals", value: record.testNumber}
                ]
            };

            var win = isc.Window.create({
                height: 400,
                width: 800,
                canDragResize: true,
                keepInParentRect: true,
                showMinimizeButton: true,
                title: record.testFile + "#" + record.testNumber,
                closeClick : function () { this.markForDestroy() },
                items: [
                    {
                        _constructor: isc.Label,
                        height: 30,
                        padding: 5,
                        contents: record.description || '(No description provided)'
                    },
                    {
                        _constructor: isc.ListGrid,
                        padding: 5,
                        dataSource: testResultDS,
                        fields: [
                            {name: "batchId", doRecordClick: true},
                            {name: "batchStartTime", doRecordClick: true},
                            {name: "result"},
                            {name: "messages"},
                            {name: "capture"}
                        ],
                        sortField: "batchStartTime",
                        sortDirection: "descending",
                        datetimeFormatter: "toSerializeableDate",
                        autoFetchData: true,
                        showFilterEditor: true,
                        initialCriteria: initialCriteria,
                        canHover: true,
                        autoFitFieldWidths: true,
                        autoFitWidthApproach: "both",
                        autoFitClipFields: ["messages"],
                        autoFitExpandField: "messages",
                        enforceVClipping: true,
                        hilites: this.hilites,
                        batchesGrid: this.creator.batchesGrid,
                        recordClick : function(viewer, record, recordNum, field, fieldNum, 
                                        value, rawValue) {
                            
                            if (! field.doRecordClick) {
                                return;
                            }

                            this.batchesGrid.targetBatch = 
                                Date.parseInput(record.batchStartTime,"YMD").getTime();
                            
                            this.batchesGrid.findTargetBatch();
                        },
                        recordDoubleClick: this.recordDoubleClick
                    }

                ]
            });

            win.show();

        },

        recordDoubleClick : function(viewer, record, recordNum, field, fieldNum, value, rawValue) {
            if (record.result == "success") return;
    
            var contents = record.details,
                multiline = ["description", "messages", "serverLogs"];

            if (multiline.indexOf(field.name) >= 0) contents = record[field.name];
  
            var win = isc.Window.create({
                height: 350,
                width: 600,
                autoCenter: true,
                showMaximizeButton: true,
                canDragResize: true,
                title: record.testFile + "#" + record.testNumber,
                closeClick : function () { this.markForDestroy() },
                showWithItems : function (items) {
                    this.items = items;
                    this.show();
                }
            });
            
            var log = isc.HTMLFlow.create({
                contents: contents
            });
                
            if (record.capture_filename) {
                var screenshot = testResultDS.getFileURL(record, "capture");    
                isc.FileLoader.cacheFiles(screenshot, function() {
                    var img = "<img src='" + screenshot + "'></img>";
                    var tabSet = isc.TabSet.create({
                        tabs: [
                            {
                                title: "Log",
                                pane: log
                            },
                            {
                                title: "Screenshot", 
                                pane: isc.HTMLFlow.create({
                                    contents: img
                                })
                            }
                        ]      
                    });
                    win.showWithItems(tabSet);
                });
            } else {
                win.showWithItems(log);
            }
        },

        hilites:
        [{criteria: {result: "success"}, textColor: "green" },
         {criteria: {result: "timeout"}, textColor: "orange"},
         {criteria: {result: "failure"}, textColor: "red"}],
    
        loadBatch : function (batchStartTime) {
            var showcase = this.targetShowcase,
                hidePass = this.targetHidePass,
                testFile = this.targetTestFile,
                fileForm = this.creator.fileForm,
                request = {};

            if (!isc.isA.Date(batchStartTime)) {
                batchStartTime = new Date(parseInt(batchStartTime));
            }
            
            var advancedCriteria = {
                _constructor:"AdvancedCriteria",
                operator:"and",
                criteria:[
                    {fieldName: "batchStartTime", operator: "equals", value: batchStartTime}
                ]
            };
            var criteria = advancedCriteria.criteria;
            if (hidePass) {
                criteria.add({fieldName: "result", operator: "notEqual", value: "success"});
            }
            if (showcase) {
                var operator = showcase.toLowerCase() == "dottest" ? "isNull" : "equals";
                criteria.add({fieldName: "showcase", operator: operator, value: showcase});
            }
            if (testFile) {
                criteria.add({fieldName: "testFile", operator: "equals", value: testFile});
            }

            //fileForm is not created until section is expanded
            if (fileForm && fileForm.getValue("operationId") === "Changes Only") {
                request.operationId = "fetchChangedFileResults";
                criteria.add({fieldName: "branch", operator: "iEquals", value: this.creator.getBranch()});
            }

            this.fetchData(advancedCriteria, null, request);
        }
    },
    
    fileFormDefaults: {
        _constructor: isc.DynamicForm,
        numCols: 1,
        fields: [
            {
                name: "operationId",
                type: "radioGroup", 
                showTitle: false, 
                vertical: false, 
                valueMap: ["All Results", "Changes Only"],
                defaultValue: "All Results",
                changed: function(form, item, value) {
                    var selected = form.creator.batchesGrid.getSelectedRecord();
                    if (! selected) {
                        return;
                    }
                    form.creator.fileGrid.loadBatch(selected.batchStartTime);
                }
            }
        ]
    },

    formDefaults: {
        _constructor: "DynamicForm",
        numCols: 2,
        cellPadding: CONSTANTS.margin,
        colWidths: [supportsTestNumber() ? "75%" : "50%", "*"],
        items: [
            // BranchesSelectItem is the branch selection picker
            {
                _constructor: "SelectItem", 
                name: "branchSelector", 
                optionDataSource: batchRunDS,
                optionOperationId: "branches",
                titleOrientation: "top", 
                title: "<b>Selected Branch to View</b>",
                width: "100%",
                
                // Toggle commented line below to switch behavior:
                // - SelectItem.defaultToFirstOption:true picks branch with most recent commit
                // - SelectItem.defaultValue allows you select a static default branch
                
                //  defaultToFirstOption: true,
                defaultValue: "MAIN",
                valueField: "branch",
            
                pickListProperties: {
                    sortField: "id",
                    sortDirection: "descending",
                    autoFitFieldWidths: true,
                    autoFitWidthApproach: "both",
                    datetimeFormatter: "toSerializeableDate"
                },
            
                pickListFields: [
                    // fields from DataSource
                    {name: "branch", autoFitWidth: true},
                    {name: "id"},
                    {name: "batchStartTime"},
                    {name: "user"          },
                    // derived stats fields
                    {name: "nPassedFiles", title: "Passed Files"},
                    {name: "nTotalFiles",  title: "Total Files" },
                    {name: "nFixes",       title: "Fixes"       },
                    {name: "nRegressions", title: "Regressions" }
                ],
                _testFields: [
                    {name: "nPassedTests", title: "Passed Tests"},
                    {name: "nTotalTests",  title: "Total Tests"}
                ],
            
                init : function () {
                    if (testResultDS.fields["testNumber"] != null) {
                        this.pickListFields.addListAt(this._testFields, 4);
                    }
                    this.Super("init", arguments);
                    this.delayCall("initStats");
                },
                initStats : function () {
                    var branch = this.getValue();
                    if (branch != null) {
                        var pickList = this.pickList;
                        if (pickList && pickList.data && pickList.data.lengthIsKnown()) {
                            this.changed(null, this, branch);  
                            this.fetchStatistics();
                            return;
                        }
                    }
                    this.delayCall("initStats");
                },
                changed : function (form, item, value) {
                    this.form.creator.batchesGrid.load(value);
                },
            
                // clicking on branch-selection picklist icon will clear the URL params
                showPickList : function (form, item, value, oldValue) {
                    if (this.form.creator.targetBranch) this.form.creator.clearURLParameters();
                    else this.Super("showPickList", arguments);
                },
            
                // load the stats for most recent batch for each branch
                fetchStatistics : function () {
                    var pickList = this.pickList;
                    for (var i = 0; i < pickList.getTotalRows(); i++) {
                        var record = pickList.getRecord(i),
                            context = { record: record, viewer: this.form.creator };
                            this.optionDataSource.fetchData({
                                id: record.id, branch: record.branch
                            }, this.fetchStatisticsReply, {
                                clientContext: context, 
                                operationId: "fetchPredecessor"
                            });
                    }
                },
                fetchStatisticsReply : function(dsResponse, data, dsRequest) {
                    var context = dsRequest.clientContext,
                        viewer  = context.viewer,
                        record1 = context.record || {},
                        record2 = data[0]        || {};
            
                    var time1 = record1.batchStartTime,
                        time2 = record2.batchStartTime;
            
                    viewer.fetchFileResults(time1, time2 ? time2 : time1, 
                                            record1, viewer.branchSelector);
                },
                installStats : function () {
                    this.pickList.markForRedraw();
                }
            },
            // add file upload controls
            { 
                name: "addNewTest",
                editorType: "ButtonItem",
                title: "Add New Test",
                click: function () { isc.FileUploadDialog.create(); },
                startRow: false,
                align: "right",
                height: 30,
                width: 100,
                colSpan: 1
            }
        ],
        
        initWidget : function() {
            this.Super("initWidget", arguments);
        }
    },

    stackDefaults: {
        _constructor: isc.SectionStack,
        visibilityMode: "multiple",
        sections: [
            {
                name: "batchesHeader",
                expanded: true,
                title: "<b>Batches for the Selected Branch</b>" + 
                    (hasResultHistory() ? " (orange batches are in progress)" : ""), 
                items: ["autoChild:batchesGrid"]
            },
            {
                name: "batchStatsHeader",
                expanded: true,
                canCollapse: false,
                title: "<b>Stats for the Selected Batch(es)</b>", 
                items: ["autoChild:statsGrid"]
            },
            {
                name: "batchLogHeader",
                expanded: true,
                title: "<b>Log Message for the Selected Batch</b>", 
                items: ["autoChild:logArea"]
            },
            {
                name: "testFileHeader",
                title: "<b>Test Results for the Selected Batch</b> " +
                    "(double click on a record to show details for failures or timeouts)", 
                items: ["autoChild:fileForm","autoChild:fileGrid"]
            }
        ],
        collapseSection : function (section) {
            if (section.name === "testFileHeader") {
                this.creator.clearBatchAndTestFileCriteria();
            }
            this.Super("collapseSection", arguments);
        }
    },

    initWidget : function () {
        this.Super("initWidget", arguments);

        var form = this.addAutoChild("form");
        var stack = this.addAutoChild("stack");

        // force creation of the autoChild on the collapsed section
        this.fileGrid = this.createAutoChild("fileGrid");

        //allow various eventHandlers, etc. a direct reference to the form's selectItem
        var branchSelector = this.branchSelector = form.getItem("branchSelector");

        var targetBranch = this.targetBranch = getURLParameter("branch");
        if (targetBranch) {
            branchSelector.setValue(targetBranch.toUpperCase());
            stack.expandSection("testFileHeader");
            stack.collapseSection("batchLogHeader");
        }
    },

    // fetch the raw data needed to compute fixes/regressions
    fetchFileResults : function(batch1StartTime, batch2StartTime, 
                                targetRecord, targetWidget) {
        var context = { 
            count: 0,
            viewer: this,
            record: targetRecord, 
            widget: targetWidget
        };
        this.testResultDS.fetchData({batchStartTime: batch1StartTime},
                                    this.fileResultsReply,
                                    {clientContext: {context: context, batch: "one"},
                                     operationId: "fetchFileResults"});
        this.testResultDS.fetchData({batchStartTime: batch2StartTime},
                                    this.fileResultsReply,
                                    {clientContext: {context: context, batch: "two"},
                                     operationId: "fetchFileResults"});
    },
    
    fileResultsReply : function(dsResponse, data, dsRequest) {
        var clientContext = dsRequest.clientContext,
            context       = clientContext.context,
            viewer        = context.viewer;

        context[clientContext.batch] = data;
        if (++context.count >= 2) {
            viewer.computeStatistics(context);
        }
    },

    // algorithms to compute fixes/regressions
    buildObjectFromFileStats : function(batchRun) {
        // create a map with all testFiles marked as initially passing
        var passedFiles = {}, 
            nPassedFiles = 0, nTotalFiles = 0;
        for (var i = 0; i < batchRun.length; i++) {
            var record = batchRun[i],
                testFile = record.testFile;
            if (passedFiles[testFile] == null) {
                passedFiles[testFile] = true;
                nPassedFiles++; nTotalFiles++;
            }
        }
        // now, sweep through all <testFile, testNumber, result> tuples
        var results = {}, 
            nPassedTests = 0, nTotalTests = 0;
        for (var i = 0; i < batchRun.length; i++) {
            var record = batchRun[i],
                testFile = record.testFile,
                testNumber = record.testNumber || 1;
            // create a binding for the <testFile, testNumber>
            results[testFile + "_" + testNumber] = record.result;
            // record the success or failure of the <testFile, testNumber>
            if (record.result == "success") nPassedTests++;
            else if (passedFiles[testFile]) {
                passedFiles[testFile] = false;
                nPassedFiles--;
            }                
            nTotalTests++;
        }
        return {results: results, 
                nPassedTests: nPassedTests, nTotalTests: nTotalTests,
                nPassedFiles: nPassedFiles, nTotalFiles: nTotalFiles};
    },

    computeStatistics : function(computationContext) {
        var target = computationContext.record,
            widget = computationContext.widget,
            batchOne = computationContext.one,
            batchTwo = computationContext.two;

        var nFixes = 0, nRegressions = 0, nNewFailures = 0;

        var mapOne = this.buildObjectFromFileStats(batchOne),
            mapTwo = this.buildObjectFromFileStats(batchTwo);

        // install intra-batch stats for current batch
        target.nTotalTests  = mapOne.nTotalTests;
        target.nPassedTests = mapOne.nPassedTests
        target.nTotalFiles  = mapOne.nTotalFiles;
        target.nPassedFiles = mapOne.nPassedFiles

        var firstResults = mapOne.results,
            secondResults = mapTwo.results;

        for (var i = 0; i < batchOne.length; i++) {
            var record = batchOne[i],
                testFile = record.testFile,
                testNumber = record.testNumber || 1;

            first = testFile + "_" + testNumber;
            var firstResult = firstResults[first];
            if (!firstResult) continue;

            var secondResult = secondResults[first];
            if (secondResult) {
                // normal case; both old and new test results exist
                if      (firstResult == "success" && secondResult != "success") nFixes++;
                else if (firstResult != "success" && secondResult == "success") nRegressions++;
            } else {
                // special case; detect initial run failure of new test
                if (firstResult != "success") {
                    var secondRootResult = secondResults[testFile + "_1"];
                    if (secondRootResult != "timeout") nNewFailures++;
                }
            }
        }

        // indicate new failures as a separate total for clarity
        if (nNewFailures > 0) nRegressions += "+" + nNewFailures;

        // finally, install inter-batch stats and update widget
        target.nFixes       = nFixes;
        target.nRegressions = nRegressions;
        widget.installStats(target);
    },

    getBranch : function () {
        return this.branchSelector.getValue();
    },

    clearURLParameters : function () {
        var href = location.href;
        location.href = href.substring(0,href.indexOf("?"));
    },

    // clear the URL-param-targeted batch and any URL-param-specified criteria
    clearBatchAndTestFileCriteria : function () {

        var batchesGrid = this.batchesGrid,
            fileGrid = this.fileGrid;

        delete fileGrid.targetShowcase;
        delete fileGrid.targetHidePass;
        delete fileGrid.targetTestFile;

        fileGrid.loadBatch(batchesGrid.targetBatch);
        
        delete batchesGrid.targetBatch;
    }

});



// create the viewer
isc.ResultViewer.create({
    ID: "resultViewer",
    batchRunDS: batchRunDS,
    testResultDS: testResultDS,
    width: "100%", 
    height: "100%",
    autoDraw: true
});
   
</SCRIPT>
</BODY>
</HTML>
