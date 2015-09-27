
/*

  SmartClient Ajax RIA system
  Version v10.0p_2014-09-18/EVAL Development Only (2014-09-18)

  Copyright 2000 and beyond Isomorphic Software, Inc. All rights reserved.
  "SmartClient" is a trademark of Isomorphic Software, Inc.

  LICENSE NOTICE
     INSTALLATION OR USE OF THIS SOFTWARE INDICATES YOUR ACCEPTANCE OF
     ISOMORPHIC SOFTWARE LICENSE TERMS. If you have received this file
     without an accompanying Isomorphic Software license file, please
     contact licensing@isomorphic.com for details. Unauthorized copying and
     use of this software is a violation of international copyright law.

  DEVELOPMENT ONLY - DO NOT DEPLOY
     This software is provided for evaluation, training, and development
     purposes only. It may include supplementary components that are not
     licensed for deployment. The separate DEPLOY package for this release
     contains SmartClient components that are licensed for deployment.

  PROPRIETARY & PROTECTED MATERIAL
     This software contains proprietary materials that are protected by
     contract and intellectual property law. You are expressly prohibited
     from attempting to reverse engineer this software or modify this
     software for human readability.

  CONTACT ISOMORPHIC
     For more information regarding license rights and restrictions, or to
     report possible license violations, please contact Isomorphic Software
     by email (licensing@isomorphic.com) or web (www.isomorphic.com).

*/

if(window.isc&&window.isc.module_Core&&!window.isc.module_Tools){isc.module_Tools=1;isc._moduleStart=isc._Tools_start=(isc.timestamp?isc.timestamp():new Date().getTime());if(isc._moduleEnd&&(!isc.Log||(isc.Log && isc.Log.logIsDebugEnabled('loadTime')))){isc._pTM={ message:'Tools load/parse time: ' + (isc._moduleStart-isc._moduleEnd) + 'ms', category:'loadTime'};
if(isc.Log && isc.Log.logDebug)isc.Log.logDebug(isc._pTM.message,'loadTime');
else if(isc._preLog)isc._preLog[isc._preLog.length]=isc._pTM;
else isc._preLog=[isc._pTM]}isc.definingFramework=true;isc.defineClass("ComponentEditor","PropertySheet");
isc.A=isc.ComponentEditor.getPrototype();
isc.A.immediateSave=false;
isc.A.itemHoverWidth=500;
isc.A.showSuperClassEvents=true;
isc.A.initialGroups=3;
isc.A.showAttributes=true;
isc.A.showMethods=false;
isc.A.basicMode=false;
isc.A.lessTitle="Less...";
isc.A.moreTitle="More...";
isc.A.canSwitchClass=false;
isc.A.componentTypeTitle="Component Type"
;

isc.A=isc.ComponentEditor.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.handlerFieldBase={
        validateOnChange:true,
        validators:[{type:"isFunction"}],
        itemHoverHTML:function(){
            var value=this.mapValueToDisplay(this.getValue());
            if(value==null)return value;
            if(value=="&nbsp;"||value.match(/^\W+$/))value="";
            return value.asHTML();
        }
    };
isc.A.itemHoverStyle="docHover";
isc.B.push(isc.A.shouldUseField=function isc_ComponentEditor_shouldUseField(field){
        if(!this.Super("shouldUseField",arguments)){
            return false;
        }
        if(field.hidden||field.inapplicable||field.advanced)return false;
        var localBasicMode=this._basicMode==null?this.basicMode:this._basicMode;
        if(localBasicMode&&!field.basic)return false;
        if(field.type&&isc.DS.isLoaded(field.type)&&field.type!="ValueMap"
             &&field.type!="Action")
         {
            return false;
        }
        var ds=isc.DS.get(this.dataSource);
        if(!ds)return true;
        var className=ds.ID,
            fieldName=field[this.fieldIdProperty];
        if(isc.jsdoc.hasData()){
            var docItem=isc.jsdoc.getDocItem(className,fieldName,true);
            if(field.visibility!=null&&docItem==null)return false;
            if(isc.isAn.XMLNode(docItem)&&docItem.getAttribute("deprecated"))return false;
            if(docItem&&isc.jsdoc.isAdvancedAttribute(docItem))return false;
        }
        return true;
    }
,isc.A.bindToDataSource=function isc_ComponentEditor_bindToDataSource(fields,componentIsDetail){
        var boundFields=this._boundFields=this.Super("bindToDataSource",arguments);
        var ds=this.dataSource?isc.DS.get(this.dataSource):null;
        if(fields&&fields.length>0)return boundFields;
        if(ds==null||this._boundFields==null)return boundFields;
        for(var i=0;i<boundFields.length;i++){
            var field=boundFields[i],
                defaultValue=field.defaultValue;
            if(defaultValue==null)continue;
            if(defaultValue=="false")defaultValue=false;
            else if(defaultValue=="true")defaultValue=true;
            else if(parseInt(defaultValue).toString()==defaultValue){
                defaultValue=parseInt(defaultValue);
            }
            field.defaultValue=defaultValue;
        }
        if(!isc.jsdoc.hasData())return boundFields;
        var groups={},createGroups=false;
        if(this.showAttributes){
            for(var i=0;i<boundFields.length;i++){
                var field=boundFields[i],
                    name=field[this.fieldIdProperty]
                ;
                var groupName=isc.jsdoc.getGroupForAttribute(ds.ID,name)||
                                    field.group||"other";
                if(groupName==null)groupName="other";
                if(groupName!="other")createGroups=true;
                if(!groups[groupName])groups[groupName]=[];
                groups[groupName].add(field);
            }
        }
        if(this.showMethods){
            if(!this.createMethodGroups(groups,ds)&&!this.showAttributes){
                return[];
            }else{
                createGroups=true;
            }
        }
        if(!createGroups){
            if(this.sortFields)boundFields.sortByProperty("name",Array.ASCENDING);
            return boundFields;
        }
        var groupNames=isc.getKeys(groups),
            dsGroupOrder=ds.getGroups(),
            groupOrder=[];
        if(dsGroupOrder!=null){
            for(var i=0;i<dsGroupOrder.length;i++){
                var index=groupNames.indexOf(dsGroupOrder[i]);
                if(index==-1)continue;
                groupNames.removeAt(index);
                groupOrder.add(dsGroupOrder[i]);
            }
            groupOrder.addList(groupNames);
        }else{
            groupOrder=groupNames;
        }
        var index=groupOrder.indexOf("other");
        if(index!=-1){
            groupOrder.removeAt(index);
            groupOrder.add("other");
        }
        fields=[];
        if(this.canSwitchClass){
            var switcherConfig=this.getClassSwitcher();
            if(switcherConfig)fields[0]=switcherConfig;
        }
        if(this.creator.shouldShowDataPathFields&&this.creator.shouldShowDataPathFields()){
            fields[fields.length]=this.getDataPathField(true);
        }
        for(var i=0;i<groupOrder.length;i++){
            var groupName=groupOrder[i],
                group=groups[groupName],
                groupItem=isc.jsdoc.getGroupItem(groupName),
                title=groupItem&&groupItem.title?groupItem.title:
                        isc.DataSource.getAutoTitle(groupName);
            if(this.sortFields)group.sortByProperty("name",Array.ASCENDING);
            fields[fields.length]=
                {
                    editorType:"TSectionItem",
                    defaultValue:title,
                    sectionExpanded:(i<this.initialGroups),
                    items:group
                };
        }
        return fields;
    }
,isc.A.addField=function isc_ComponentEditor_addField(field,index){
        if(this.fields)this.fields.addAt(field,index);
    }
,isc.A.getDataPathField=function isc_ComponentEditor_getDataPathField(isInput){
        var creator=this.creator,
            grid=creator.operationsPalette,
            initData=grid?grid.data:null,
            data=creator.trimOperationsTreeData(initData,isInput)
        ;
        return{
            name:isInput?"inputDataPath":"dataPath",
            title:isInput?"Input DataPath":"DataPath",
            isInput:isInput,
            type:"DataPathItem",
            operationsPalette:grid,
            operationsTreeData:data
        };
    }
,isc.A.getClassSwitcher=function isc_ComponentEditor_getClassSwitcher(){
        var dataSource=isc.DS.get(this.dataSource),
            classObj=isc.ClassFactory.getClass(dataSource.ID);
        if(!classObj)return null;
        return{
            name:"classSwitcher",
            title:this.componentTypeTitle,
            defaultValue:classObj.getClassName(),
            type:"select",
            valueMap:this.getClassSwitcherValueMap(dataSource,classObj)
        };
    }
,isc.A.getClassSwitcherValueMap=function isc_ComponentEditor_getClassSwitcherValueMap(dataSource,classObj){
        var chain,
            valueMap=[];
        if(classObj)chain=this.getInheritanceChain(classObj,dataSource);
        if(!chain)return null;
        for(var i=0;i<chain.length;i++){
            var schema=isc.DS.getNearestSchema(chain[i].getClassName()),
                subs=schema.substituteClasses;
                if(schema.createStandalone!=false){
                    if(!valueMap.contains(chain[i].getClassName())){
                        valueMap.add(chain[i].getClassName());
                    }
                }
            if(!subs)continue;
            var subsArray=subs.split(",");
             for(var i=0;i<subsArray.length;i++){
                subsArray[i]=subsArray[i].trim();
                if(!valueMap.contains(subsArray[i]))valueMap.add(subsArray[i]);
            }
        }
        valueMap.sort();
        return valueMap;
    }
,isc.A.createMethodGroups=function isc_ComponentEditor_createMethodGroups(groups,dataSource){
        var classObj=isc.ClassFactory.getClass(dataSource.ID);
        this._editableMethodFields=[];
        var localBasicMode=this._basicMode==null?this.basicMode:this._basicMode;
        if(!localBasicMode&&classObj&&classObj._stringMethodRegistry&&
            !isc.isAn.emptyObject(classObj._stringMethodRegistry))
        {
            var chain=this.getInheritanceChain(classObj,dataSource),
                classMethods,
                superclassMethods=[],
                newMethods,
                methodGroups={}
            ;
            for(var i=0;i<chain.length;i++){
                var currentClassObj=chain[i];
                classMethods=isc.getKeys(currentClassObj._stringMethodRegistry);
                newMethods=classMethods.duplicate()
                newMethods.removeList(superclassMethods);
                superclassMethods=classMethods;
                if(newMethods.length==0)continue;
                var groupName=
                    (currentClassObj==isc.Canvas?"Basic":currentClassObj.getClassName())
                    +" Methods";
                methodGroups[groupName]=[];
                for(var j=0;j<newMethods.length;j++){
                    var methodName=newMethods[j];
                    var docRef="method:"+currentClassObj.getClassName()+"."+methodName,
                        docItem=isc.jsdoc.getDocItem(docRef);
                    if(!docItem){
                        if(!dataSource.methods||!dataSource.methods.find("name",methodName)){
                            continue;
                        }
                    }
                    if(docItem&&isc.jsdoc.getAttribute(docItem,"deprecated"))continue;
                    var field=this.getMethodField(newMethods[j]);
                    methodGroups[groupName].add(field);
                }
                if(methodGroups[groupName].length==0){
                    delete methodGroups[groupName];
                    delete groups[groupName];
                }
            }
            var methodGroupsNames=isc.getKeys(methodGroups).reverse();
            for(var i=0;i<methodGroupsNames.length;i++){
                groups[methodGroupsNames[i]]=methodGroups[methodGroupsNames[i]];
            }
            return true;
        }
        if(dataSource.methods&&dataSource.methods.length>0){
            var methodFields=groups[dataSource.ID+localBasicMode?
                                                            " Basic":""+" Methods"]=[];
            for(var i=0;i<dataSource.methods.length;i++){
                var method=dataSource.methods[i];
                if(localBasicMode&&!method.basic)continue;
                var field=this.getMethodField(method.name);
                methodFields.add(field);
            }
            return true;
        }
        return false;
    }
,isc.A.getInheritanceChain=function isc_ComponentEditor_getInheritanceChain(classObj,dataSource){
        var chain=[],
            showSuper=this._firstNonNull(dataSource.showSuperClassEvents,
                                           this.showSuperClassEvents);
        if(showSuper&&
            (classObj.isA("Canvas")||classObj.isA("FormItem"))){
            for(var currentClassObj=classObj;
                 currentClassObj!=isc.Class;
                 currentClassObj=currentClassObj.getSuperClass())
            {
                chain.add(currentClassObj);
            }
        }
        chain.reverse();
        return chain;
    }
,isc.A.getMethodField=function isc_ComponentEditor_getMethodField(methodName){
        var field=isc.clone(this.handlerFieldBase);
        field[this.fieldIdProperty]=methodName;
        field.type=this.canEditExpressions?"expression":"action";
        this._editableMethodFields.add(field);
        return field;
    }
,isc.A.clearComponent=function isc_ComponentEditor_clearComponent(){
        var comp=this.currentComponent;
        if(comp==null)return;
        delete this.currentComponent;
        delete this.dataSource;
        this.setFields([]);
    }
,isc.A.editComponent=function isc_ComponentEditor_editComponent(component,liveObject){
        var type=component.type,
            liveObject=liveObject||component.liveObject;
        if(liveObject.useCustomSchema)type=liveObject.useCustomSchema;
        this.currentComponent=component;
        if(this.logIsInfoEnabled("editing")){
            this.logInfo("Editing component of type: "+type+
                         ", defaults: "+this.echo(component.defaults)+
                         ", liveObject: "+this.echoLeaf(liveObject),"editing");
        }
        if(component.advancedMode)this._basicMode=false;
        this.setDataSource(type);
        var values={},
            editableFields=this._boundFields
        ;
        if(this._editableMethodFields){
            editableFields=editableFields.concat(this._editableMethodFields);
        }
        var editProperties=(!liveObject||!liveObject.getEditableProperties)
                    ?component.defaults:liveObject.getEditableProperties(editableFields);
        for(var i=0;i<editableFields.length;i++){
            var item=editableFields[i];
            if(item.advanced){
                item.showIf=this._falseFunc;
            }
            if(!item.name)continue;
            var propertyName=item.name,
                value=editProperties[propertyName];
            var undef;
            if(value===undef)continue;
            if(isc.isA.Function(value)){
                if(!liveObject.getClass)continue;
                var baseImpl=liveObject.getClass().getInstanceProperty(propertyName);
                if(baseImpl==value)continue;
            }
            values[propertyName]=value;
        }
        if(this.logIsDebugEnabled("editing")){
            this.logDebug("Live values: "+this.echo(values),"editing");
        }
        this.setValues(values);
        for(var propertyName in values){
            if(isc.isA.Function(values[propertyName])){
                this.setValue(propertyName,values[propertyName]);
            }
        }
        if(component.defaults.dataPath&&this.getItem("dataPath")){
            this.getItem("dataPath").setDataPathProperties(component);
        }
        if(component.defaults.inputDataPath&&this.getItem("inputDataPath")){
            this.getItem("inputDataPath").setDataPathProperties(component);
        }
    }
,isc.A._falseFunc=function isc_ComponentEditor__falseFunc(){

        return false;
    }
,isc.A.wrapEditorColumns=function isc_ComponentEditor_wrapEditorColumns(){
        if(!this.items)return;
        var visibleCount=0;
        for(var i=0;i<this.items.length;i++){
            var item=this.items[i];
            if(item.visible&&!item.advanced)visibleCount++;
        }
        if(visibleCount>10)this.numCols=4;
        if(visibleCount>20)this.numCols=6;
    }
,isc.A.titleHoverHTML=function isc_ComponentEditor_titleHoverHTML(item){
        if(isc.jsdoc.hasData()){
            var html=isc.jsdoc.hoverHTML(this.dataSource,item.name);
            if(!html){
                if(this.showMethods){
                    var method=isc.jsdoc.docItemForDSMethod(this.dataSource,item.name);
                    if(method)html=isc.MethodFormatter.hoverHTML(method);
                }else{
                    var field=isc.jsdoc.docItemForDSField(this.dataSource,item.name);
                    if(field)html=isc.AttrFormatter.hoverHTML(field);
                }
            }
            if(html)return html;
        }
        return"<nobr><code><b>"+item.name+"</b></code> (no doc available)</nobr>";
    }
,isc.A.getEditorType=function isc_ComponentEditor_getEditorType(item){
        if(item&&item.type=="ValueMap")return"ValueMapItem";
        var baseType=this.Super("getEditorType",arguments);
        baseType=isc.FormItemFactory.getItemClass(baseType).getClassName();
        var toolType="T"+baseType;
        if(isc[toolType]!=null&&isc.isA.FormItem(isc[toolType]))return toolType;
        return baseType;
    }
);
isc.B._maxIndex=isc.C+15;

isc.defineClass("Wizard","VLayout");
isc.A=isc.Wizard.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.stepInstructionsDefaults={
        _constructor:"Label",
        contents:"Instructions",
        padding:10,
        height:20
    };
isc.A.stepPaneDefaults={
        _constructor:"VLayout",
        padding:10
    };
isc.A.showStepIndicator=false;
isc.A.stepIndicatorDefaults={
        _constructor:"HLayout",
        height:22,
        layoutMargin:0,
        layoutLeftMargin:10,
        membersMargin:2
    };
isc.A.stepIndicatorItems=[];
isc.A.stepButtonDefaults={
        _constructor:"Img",
        layoutAlign:"center",
        showRollOver:false,
        height:18,
        width:18
    };
isc.A.stepSeparatorDefaults={
        _constructor:"Img",
        layoutAlign:"center",
        height:16,
        width:16,
        src:"[SKIN]/TreeGrid/opener_closed.gif"
    };
isc.A.navButtonsDefaults={
        _constructor:"ToolStrip",
        height:22,
        layoutMargin:5,
        membersMargin:10
    };
isc.A.navButtonsItems=["previousButton","nextButton","finishButton","cancelButton"];
isc.A.previousButtonDefaults={
        _constructor:"Button",
        layoutAlign:"center",
        title:"Previous",
        click:"this.creator.previousStep()",
        visibility:"hidden"
    };
isc.A.nextButtonDefaults={
        _constructor:"Button",
        layoutAlign:"center",
        title:"Next",
        click:"this.creator.nextStep()"
    };
isc.A.finishButtonDefaults={
        _constructor:"Button",
        layoutAlign:"center",
        title:"Finish",
        click:"this.creator.finished()",
        visibility:"hidden"
    };
isc.A.cancelButtonDefaults={
        _constructor:"Button",
        layoutAlign:"center",
        title:"Cancel",
        click:"this.creator.cancel()"
    };
isc.A.autoChildParentMap={
        nextButton:"navButtons",
        previousButton:"navButtons",
        finishButton:"navButtons"
    };
isc.A._$stepButton="_stepButton_";
isc.B.push(isc.A.initWidget=function isc_Wizard_initWidget(){
        this.Super("initWidget");
        this.createSteps();
        this.addAutoChild("stepInstructions");
        this.addAutoChild("stepPane");
        this.addAutoChild("navButtons");
        this.addAutoChildren(this.navButtonsItems,this.navButtons);
        if(this.showStepIndicator){
            this.addAutoChild("stepIndicator");
            for(var i=0;i<this.steps.length;i++){
                var stepName=this.steps[i].stepName,
                    stepButtonProperties={src:stepName}
                ;
                var stepButton=this.createAutoChild("stepButton",stepButtonProperties);
                this.stepIndicator.addMember(stepButton);
                this.steps[i]._stepButton=stepButton;
                if(i+1<this.steps.length){
                    this.stepIndicator.addMember(this.createAutoChild("stepSeparator"));
                }
            }
            this.navButtons.addMember(this.stepIndicator,0);
        }
        this.goToStep(0,true);
    }
,isc.A.draw=function isc_Wizard_draw(showing){
        var returnValue=this.Super("draw",arguments);
        this.updateButtons();
        return returnValue;
    }
,isc.A.createSteps=function isc_Wizard_createSteps(steps){
        if(!steps)steps=this.steps;
        if(!steps)return;
        if(!isc.isAn.Array(steps))steps=[steps];
        for(var i=0;i<steps.length;i++){
            steps[i]=isc.WizardStep.create(steps[i],{wizard:this});
        }
    }
,isc.A.getStep=function isc_Wizard_getStep(stepId){return isc.Class.getArrayItem(stepId,this.steps)}
,isc.A.getCurrentStep=function isc_Wizard_getCurrentStep(){return this.getStep(this.currentStepNum);}
,isc.A.getCurrentStepIndex=function isc_Wizard_getCurrentStepIndex(){return this.currentStepNum;}
,isc.A.getStepIndex=function isc_Wizard_getStepIndex(stepId){return isc.Class.getArrayItemIndex(stepId,this.steps)}
,isc.A.getStepPane=function isc_Wizard_getStepPane(stepId){
        return this.getStep(stepId).pane;
    }
,isc.A.goToStep=function isc_Wizard_goToStep(stepId,firstStep){
        if(!firstStep){
            if(!this.getCurrentStep().exitStep(stepId))return;
            this.getStepPane(this.currentStepNum).hide();
        }
        var step=this.getStep(stepId);
        step.enterStep(this.currentStepNum);
        this.currentStepNum=this.getStepIndex(step);
        var pane=this.getStepPane(stepId);
        if(step.instructions)this.stepInstructions.setContents(step.instructions);
        else this.stepInstructions.hide();
        this.stepPane.addMember(pane,0);
        pane.show();
        this.updateButtons();
    }
,isc.A.go=function isc_Wizard_go(direction){
        var index=this.getStepIndex(this.currentStepNum);
        index+=direction;
        this.goToStep(this.getStep(index));
    }
,isc.A.nextStep=function isc_Wizard_nextStep(){
        var currentStep=this.getStep(this.currentStepNum);
        if(currentStep.hasNextStep())this.goToStep(currentStep.getNextStep());
        else this.go(1);
    }
,isc.A.previousStep=function isc_Wizard_previousStep(){
        var currentStep=this.getStep(this.currentStepNum);
        if(currentStep.hasPreviousStep())this.goToStep(currentStep.getPreviousStep());
        else this.go(-1);
    }
,isc.A.finished=function isc_Wizard_finished(){
        this.resetWizard();
    }
,isc.A.cancel=function isc_Wizard_cancel(){
        this.resetWizard();
    }
,isc.A.updateButtons=function isc_Wizard_updateButtons(){
        var stepNum=this.getStepIndex(this.currentStepNum),
            step=this.getCurrentStep()
        ;
        if(this.stepIndicator){
            for(var i=0;i<this.steps.length;i++){
                var stepButton=this.steps[i]._stepButton;
                if(stepNum>i){
                    stepButton.setState("");
                }else if(stepNum==i){
                    stepButton.setState("Down");
                }else{
                    stepButton.setState("Disabled");
                }
            }
        }
        if(stepNum==0||this.forwardOnly||!step.hasPreviousStep())this.previousButton.hide();
        else this.previousButton.show();
        if(!step.hasNextStep()||stepNum==this.steps.length-1){
            this.nextButton.hide();
            this.finishButton.show();
        }else{
            this.nextButton.show();
            this.finishButton.hide();
        }
    }
,isc.A.resetWizard=function isc_Wizard_resetWizard(){
        this.goToStep(0);
    }
);
isc.B._maxIndex=isc.C+16;

isc.defineClass("WizardStep").addMethods({
    enterStep:function(previousStepId){},
    exitStep:function(nextStepId){return true;},
    hasNextStep:function(){
        for(var i=this.wizard.getStepIndex(this.ID)+1;i<this.wizard.steps.length;i++)
            if(!this.wizard.getStep(i).hidden)return true;
        return false;
    },
    getNextStep:function(){
        for(var i=this.wizard.getStepIndex(this.ID)+1;i<this.wizard.steps.length;i++)
            if(!this.wizard.getStep(i).hidden)return i;
        return-1;
    },
    hasPreviousStep:function(){
        for(var i=this.wizard.getStepIndex(this.ID)-1;i>=0;i--)
            if(!this.wizard.getStep(i).hidden)return true;
        return false;
    },
    getPreviousStep:function(){
        for(var i=this.wizard.getStepIndex(this.ID)-1;i>=0;i--)
            if(!this.wizard.getStep(i).hidden)return i;
        return-1;
    },
    show:function(){
        this.hidden=false;
        this.wizard.updateButtons();
    },
    hide:function(){
        this.hidden=true;
        this.wizard.updateButtons();
        if(this.wizard.getCurrentStep()==this){
            var newStep=this.getPreviousStep();
            if(newStep==-1)newStep=this.getNextStep();
            this.wizard.goToStep(newStep);
        }
    }
});
isc.DataSource.create({
    ID:"isc_XMethodsServices",
    dataURL:"shortServiceListing.xml",
    recordName:"service",
    recordXPath:"/default:inspection/default:service",
    fields:[
        {name:"abstract",title:"Description"},
        {name:"xMethodsPage",title:"Site",type:"link",width:50,
          valueXPath:".//wsilxmethods:serviceDetailPage/@location"
        },
        {name:"wsdlURL",title:"WSDL",type:"link",width:50,
          valueXPath:
             "default:description[@referencedNamespace='http://schemas.xmlsoap.org/wsdl/']/@location"
        }
    ]
});
isc.defineClass("DSWizardBase","VLayout");
isc.A=isc.DSWizardBase.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.autoChildParentMap={
    nextButton:"navToolbar",
    previousButton:"navToolbar",
    finishButton:"navToolbar"
};
isc.B.push(isc.A.initWidget=function isc_DSWizardBase_initWidget(){
    this.Super("initWidget");
    this.addAutoChild("stepInstructions",{
        contents:"Instructions",
        padding:4,
        height:20,
        wrap:false,
        overflow:"visible"
    },isc.Label);
    this.addAutoChild("navToolbar",{
        height:22,
        layoutMargin:10,
        membersMargin:10
    },isc.HLayout);
    this.addAutoChild("previousButton",{
        title:"< Previous",
        click:"this.creator.previousPage()",
        visibility:"hidden"
    },isc.Button);
    this.navToolbar.addMember(isc.LayoutSpacer.create());
    this.addAutoChild("nextButton",{
        title:"Next >",
        click:"this.creator.nextPage()",
        disabled:true,
        setDisabled:function(disabled){
            var returnval=this.Super('setDisabled',arguments);
            this.creator._nextButtonDisabled(disabled);
        }
    },isc.Button);
    this.addAutoChild("finishButton",{
        title:"Finish",
        click:"this.creator.finish()",
        visibility:"hidden"
    },isc.Button);
    this.goToPage(0,true);
}
,isc.A.getPage=function isc_DSWizardBase_getPage(pageId){return isc.Class.getArrayItem(pageId,this.pages)}
,isc.A.getCurrentPage=function isc_DSWizardBase_getCurrentPage(){return this.getPage(this.currentPageNum);}
,isc.A.getPageIndex=function isc_DSWizardBase_getPageIndex(pageId){return isc.Class.getArrayItemIndex(pageId,this.pages)}
,isc.A.getPageView=function isc_DSWizardBase_getPageView(pageName,enteringPage){
    var page=this.getPage(pageName),
        pageId=page.ID;
    if(!pageId)return page.view;
    if(enteringPage){
        var enterFunction="enter"+pageId;
        if(this[enterFunction])this[enterFunction](page,pageId);
        else this.enterPage(page,pageId);
    }
    this.logWarn("for page: "+this.echoLeaf(pageName)+
                 " got pageId: "+pageId+
                 (enteringPage&&this[enterFunction]?
                    " called enter function: "+enterFunction:"")+
                 ", view is: "+page.view);
    return page.view;
}
,isc.A.enterPage=function isc_DSWizardBase_enterPage(page,pageId){}
,isc.A.goToPage=function isc_DSWizardBase_goToPage(pageId,firstPage){
    if(!firstPage){
        this.getPageView(this.currentPageNum).hide();
    }
    var page=this.getPage(pageId);
    this.currentPageNum=this.getPageIndex(page);
    var view=this.getPageView(pageId,true);
    if(page.instructions)this.stepInstructions.setContents(page.instructions);
    else this.stepInstructions.hide();
    this.addMember(view,1);
    view.show();
    this.updateButtons();
}
,isc.A.go=function isc_DSWizardBase_go(direction){
    var index=this.getPageIndex(this.currentPageNum);
    index+=direction;
    this.goToPage(this.getPage(index));
}
,isc.A.nextPage=function isc_DSWizardBase_nextPage(){
    var currentPage=this.getPage(this.currentPageNum);
    if(currentPage.nextPage)this.goToPage(currentPage.nextPage);
    else this.go(1);
}
,isc.A.previousPage=function isc_DSWizardBase_previousPage(){
    var currentPage=this.getPage(this.currentPageNum);
    if(currentPage.previousPage)this.goToPage(currentPage.previousPage);
    else this.go(-1);
}
,isc.A.finish=function isc_DSWizardBase_finish(){
    this.hide();
    this.resetWizard();
}
,isc.A.updateButtons=function isc_DSWizardBase_updateButtons(){
    var pageNum=this.getPageIndex(this.currentPageNum);
    if(pageNum==0)this.previousButton.hide();
    else this.previousButton.show();
    if(this.getPage(pageNum).endPage||pageNum==this.pages.length-1){
        this.nextButton.hide();
        this.finishButton.show();
    }else{
        this.nextButton.setDisabled(this.nextButtonIsDisabled(pageNum));
        this.nextButton.show();
        this.finishButton.hide();
    }
}
,isc.A._nextButtonDisabled=function isc_DSWizardBase__nextButtonDisabled(disabled){
    if(!this._nextEnabledMap)this._nextEnabledMap=[];
    this._nextEnabledMap[this.currentPageNum]=!disabled;
}
,isc.A.nextButtonIsDisabled=function isc_DSWizardBase_nextButtonIsDisabled(pageNum){
    return this._nextEnabledMap?!this._nextEnabledMap[pageNum]:true;
}
,isc.A.resetWizard=function isc_DSWizardBase_resetWizard(){
    delete this._nextEnabledMap;
    this.goToPage(0);
}
);
isc.B._maxIndex=isc.C+15;

isc.defineClass("DSWizard","DSWizardBase");
isc.A=isc.DSWizard.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.pages=[
    {ID:"StartPage",
      instructions:"Select the source of data to bind to:"
    },
    {ID:"PickOperationPage",
      instructions:"Select a public Web Service, or enter a WSDL file URL.  Then select"+
                   " the operation to invoke"
    },
    {ID:"CallServicePage",
      instructions:"Use the provided form to invoke the web service and obtain a sample"+
                   " result, then select an approriate element set for list binding"
    },
    {ID:"BindingPage",
      instructions:"Below is a default binding to a ListGrid.  Use the field editor to "+
                   "customize the binding",
      endPage:true
    }
    ,
    {
      ID:"SFPickEntityPage",
      instructions:"Choose an object type you would like to use in SmartClient applications"
    },
    {
      ID:"SFDonePage",
      instructions:"Below is an example of a grid bound to the chosen SForce Object",
      endPage:true
    },
    {
      ID:"KapowPickRobotPage",
      instructions:"Choose the Kapow Robot(s) you would like to use in SmartClient applications"
    }
];
isc.A.servicePickerDefaults={
    recordClick:function(viewer,record,recordNum){
        var wsdlURL=this.getRawCellValue(record,recordNum,this.getFieldNum("wsdlURL"));
        this.logWarn("wsdlURL is: "+wsdlURL);
        this.creator.fetchWSDL(wsdlURL);
    }
};
isc.A.operationPickerDefaults={
    recordClick:function(viewer,record,recordNum){
        var operationName=this.getRawCellValue(record,recordNum,this.getFieldNum("name"));
        this.creator.wsdlDoc=this.data.document;
        this.creator.operationName=operationName;
        this.creator.nextButton.enable();
    },
    alternateRecordStyles:true
};
isc.B.push(isc.A.enterStartPage=function isc_DSWizard_enterStartPage(page){
    if(!this.dsTypePicker){
        this.createDSTypePicker();
        page.view=this.dsTypePicker;
    }
    this.nextButton.setDisabled(this.dsTypePicker.getValue("dsType")==null);
}
,isc.A.createDSTypePicker=function isc_DSWizard_createDSTypePicker(){
    this.dsTypePicker=this.createAutoChild("dsTypePicker",{
        layoutAlign:"center",
        width:350,
        showHeader:false,
        selectionType:"single",
        leaveScrollbarGap:false,
        width:300,
        showAllRecords:true,
        bodyOverflow:"visible",
        overflow:"visible",
        selectionChanged:function(){
            this.creator.nextButton.setDisabled(!this.anySelected());
        },
        getValue:function(){
            var record=this.getSelectedRecord();
            if(!record)return null;
            return record.name;
        },
        clearValues:function(){
            this.deselectAllRecords();
        },
        defaultEditContext:isc.EditPane.create({height:0}),
        recordDoubleClick:function(){
            this.creator.nextPage();
        }
    },isc.TreePalette);
    var wizardsDS=isc.DataSource.create({
        recordXPath:"/PaletteNodes/PaletteNode",
        fields:{
            name:{name:"name",type:"text",length:8,required:true},
            title:{name:"title",type:"text",title:"Title",length:128,required:true},
            className:{name:"className",type:"text",title:"Class Name",length:128,required:true},
            icon:{name:"icon",type:"image",title:"Icon Filename",length:128},
            iconWidth:{name:"iconWidth",type:"number",title:"Icon Width"},
            iconHeight:{name:"iconHeight",type:"number",title:"Icon Height"},
            iconSize:{name:"iconSize",type:"number",title:"Icon Size"},
            showDropIcon:{name:"showDropIcon",type:"boolean",title:"Show Drop Icon"},
            defaults:{name:"defaults",type:"Canvas",propertiesOnly:true},
            children:{name:"children",type:"paletteNode",multiple:true}
        }
    });
    if(this.callingBuilder){
        wizardsDS.dataURL=this.callingBuilder.workspacePath+"/../dataSourceWizards.xml";
        var _this=this;
        wizardsDS.fetchData({},function(dsResponse,data){
            _this.fetchWizardsReply(data);
            _this.openWizardTree();
        });
    }
}
,isc.A.fetchWizardsReply=function isc_DSWizard_fetchWizardsReply(data){
    this.dsTypePicker.data.addList(data,this.dsTypePicker.data.getRoot());
}
,isc.A.openWizardTree=function isc_DSWizard_openWizardTree(data){
    var tree=this.dsTypePicker.data;
    tree.openAll();
}
,isc.A.nextPage=function isc_DSWizard_nextPage(){
    var dsType=this.dsTypePicker.getValue(),
        record=this.dsTypePicker.getSelectedRecord();
        _this=this;
    this.dsTypeRecord=record;
    if(this.currentPageNum==0){
        if(record.wizardConstructor){
            if(!record.wizardDefaults){
                record.wizardDefaults={};
            }
            record.wizardDefaults.width="80%";
            record.wizardDefaults.height="80%";
            record.wizardDefaults.autoCenter=true;
            record.wizardDefaults.showDataView=true;
            this.dsTypePicker.defaultEditContext.requestLiveObject(record,function(results){
                _this.showDSEditor(results,true,instructions);
            },this.dsTypePicker);
            if(this.callingBuilder)this.callingBuilder.wizardWindow.hide();
            return;
        }
        if(record&&record.className=="JavaBean"){
            var _this=this,
                defaults=record?record.wizardDefaults:{};
            if(!defaults||!defaults.serverConstructor){
                isc.say("NOTE: This wizard <b>does not generate a fully functioning "+
                    "DataSource</b>; it creates a DataSource descriptor (.ds.xml file) which "+
                    "is ready to be loaded and bound to UI components, but does not provide "+
                    "CRUD functionality (search and editing of objects)."+
                    "<P>"+
                    "If you are using SQL or Hibernate, use the SQL or Hibernate wizards "+
                    "instead to generate a fully functional DataSource.  Otherwise, read the "+
                    "<a target='_blank' "+
                    "href='http://localhost:8080/isomorphic/system/reference/SmartClient_Reference.html#group..clientServerIntegration'>"+
                    "Client-Server Integration</a> topic in the <i>SmartClient Reference</i> "+
                    "to learn how to create a custom DataSource connector.",
                    function(){
                        _this.startJavaBeanWizard(_this,record);
                    }
                );
                return;
            }
            this.startJavaBeanWizard(this,record);
            return;
        }
        if(dsType=="sforce"){
            var wizard=this,
                service=isc.WebService.get("urn:partner.soap.sforce.com");
            service.ensureLoggedIn(
                function(){wizard.goToPage("SFPickEntityPage");},
                true
            );
            return;
        }else if(dsType=="kapow"){
            var wizard=this;
            if(!this.robotServerPicker)this.robotServerPicker=isc.RobotServerPicker.create({
                robotServerSelected:function(){wizard.goToPage("KapowPickRobotPage");}
            });
            this.robotServerPicker.show();
            return;
        }else if(dsType=="webService"){
            var wizard=this;
            var nextButton=isc.IButton.create({
                autoShow:false,
                title:"Next",
                autoFit:true,
                click:function(){wizard.servicePicker.hide();wizard.pickOperation()}
            });
            if(!this.servicePicker)this.servicePicker=isc.Dialog.create({
                title:"Enter WSDL Webservice URL",
                isModal:true,
                autoShow:false,
                autoSize:true,
                autoCenter:true,
                bodyDefaults:{padding:10},
                items:[
                    isc.DynamicForm.create({
                        autoShow:false,
                        values:{serviceURL:"http://"},
                        itemKeyPress:function(item,keyName){
                            if(keyName=='Enter'){
                                nextButton.click();
                            }
                        },
                        items:[
                            {name:"serviceURL",title:"WSDL URL",type:"text",width:400}
                        ]
                    }),
                    isc.LayoutSpacer.create({height:10}),
                    isc.HLayout.create({
                        height:1,
                        membersMargin:5,
                        members:[
                            nextButton,
                            isc.IButton.create({
                                autoShow:false,
                                title:"Cancel",
                                autoFit:true,
                                click:function(){wizard.servicePicker.hide();}
                            })
                        ]
                    })
                ]
            });
            this.servicePicker.show();
            return;
        }else if(dsType!="webService"){
            var props,
                instructions;
            if(dsType.contains("Hibernate")){
                instructions="Each field you enter below corresponds to a database column "+
"of the same name.  The table name will be the same as the DataSource ID by default, or you "+
"may enter a Table Name below.  Hibernate database settings are in "+
"[webroot]/WEB-INF/classes/hibernate.cfg.xml"
                props={
                    dataFormat:"iscServer",
                    serverType:"hibernate"
                };
            }else if(dsType.contains("SQL")){
                instructions="Each field you enter below corresponds to a database column "+
"of the same name.  The table name will be the same as the DataSource ID by default, or you "+
"may enter a Table Name below.  By default, the default DataBase shown in the Admin Console "+
"will be used, or you may enter \"Database Name\" below.";
                props={
                    dataFormat:"iscServer",
                    serverType:"sql"
                };
            }else if(dsType=="simpleXML"){
                instructions="For \"dataURL\", enter a URL which will return XML data.<P>"+
"For \"recordXPath\", enter an XPath that will select the XML tags you wish to use as rows. "+
"For example, if the tag you want is named \"Person\", a recordXPath of \"//Person\" will "+
"work for most simple XML formats.<P>"+
"Enter fields named after the subelements and attributes of the tag used for rows.  Click "+
"the \"More\" button to see more field properties and documentation, particularly \"valueXPath\"";
                props={dataFormat:"xml"};
            }else if(dsType=="json"){
                instructions=
"For \"dataURL\", enter a URL which will return JSON data.<P>"+
"For \"recordXPath\", enter an XPath to an Array of Objects in the JSON data, then enter fields for each property of those Objects which you want to display, and its type.<P>"+
"Click the \"More\" button to see more field properties and documentation, particularly \"valueXPath\"";
                props={dataFormat:"json"};
            }else if(dsType=="rss"){
                instructions="Enter the URL of the RSS feed as \"dataURL\" below, then add or remove fields.";
                props={
                    dataFormat:"xml",
                    recordXPath:"//default:item|//item",
                    fields:[
                        {name:"title",title:"Title"},
                        {name:"link",title:"Story",type:"link"},
                        {name:"description",title:"Description"},
                        {name:"pubDate",title:"Published"}
                    ]
                }
            }
            this.showDSEditor(props,true,instructions);
            return;
        }
    }
    this.Super("nextPage");
}
,isc.A.pickOperation=function isc_DSWizard_pickOperation(){
    isc.showPrompt("Loading WSDL...");
    isc.XML.loadWSDL(this.servicePicker.items[0].getValue("serviceURL"),
        this.getID()+".webServiceLoaded(service)",
        null,
        true
    );
}
,isc.A.webServiceLoaded=function isc_DSWizard_webServiceLoaded(service){
    isc.clearPrompt();
    if(service){
        this.servicePicker.items[0].setValue("serviceURL","http://");
        var vb=this.callingBuilder;
        if(!vb.operationsPalette){
            if(vb.showRightStack!=false){
                vb.showOperationsPalette=true;
                vb.addAutoChild("operationsPalette");
                vb.rightStack.addSection({title:"Operations",autoShow:true,
                    items:[vb.operationsPalette]
                },1);
            }
            for(var i=0;i<service.portTypes.length;i++){
                var portType=service.portTypes[i];
                for(var j=0;j<portType.operation.length;j++){
                    var operation=portType.operation[j];
                    var soConfig={
                        operationName:operation.name,
                        serviceNamespace:service.serviceNamespace,
                        serviceName:service.serviceName,
                        serviceDescription:service.serviceName||service.serviceNamespace,
                        portTypeName:portType.portTypeName,
                        location:service.location
                    }
                    vb.addWebService(service,soConfig);
                }
            }
        }
        vb.wizardWindow.hide();
    }
}
,isc.A.fetchWSDL=function isc_DSWizard_fetchWSDL(wsdlURL){
    this.wsdlURL=wsdlURL;
    if(wsdlURL!=null){
        if(isc.isA.ResultSet(this.operationPicker.data)){
            this.operationPicker.data.invalidateCache();
        }
        this.operationPicker.fetchData(null,null,{dataURL:wsdlURL});
    }
}
,isc.A.enterCallServicePage=function isc_DSWizard_enterCallServicePage(page){
    var wsdlURL=this.wsdlURL;
    isc.xml.loadWSDL(wsdlURL,this.getID()+"._wsdlLoaded(service)");
    if(this.serviceInput!=null)return;
    var view=this.createAutoChild("callServicePage",{
        visibilityMode:"multiple"
    },isc.SectionStack);
    page.view=view;
    this.serviceInput=this.createAutoChild("serviceInput",{
    },isc.DynamicForm);
    var callServiceButton=this.createAutoChild("callServiceButton",{
        title:"Call Service",
        click:"this.creator.callService()",
        resizeable:false
    },isc.Button);
    view.addSection({title:"Service Inputs",autoShow:true,items:[
        this.serviceInput,
        callServiceButton
    ]});
    this.requestEditor=this.createAutoChild("requestEditor",{
        height:250,
        fields:[
            {name:"useEditedMessage",title:"Use Edited Message",type:"checkbox",
             defaultValue:false},
            {name:"requestBody",showTitle:false,type:"textArea",width:"*",height:"*",
             colSpan:"*"}
        ]
    },isc.DynamicForm);
    view.addSection({title:"Request Editor",items:[this.requestEditor]});
    this.serviceOutput=this.createAutoChild("serviceOutput",{
        showHeader:false,
        wrapCells:true,
        fixedRecordHeights:false
    },isc.DOMGrid);
    view.addSection({title:"Service Output",autoShow:true,items:[this.serviceOutput]});
    this.expressionForm=this.createAutoChild("expressionForm",{
        numCols:4,
        colWidths:[120,150,"*",50],
        items:[
            {name:"selectBy",title:"Select Records By",width:"*",
             valueMap:{tagName:"Tag Name",xpath:"XPath Expression"},
             defaultValue:"xpath"},
            {name:"expression",showTitle:false,width:"*"},
            {type:"button",title:"Select",width:"*",startRow:false,
             click:"form.creator.selectNodes()"}
        ]
    },isc.DynamicForm);
    this.selectedNodesView=this.createAutoChild("selectedNodesView",{
        showHeader:false,
        showRoot:false,
        wrapCells:true,
        fixedRecordHeights:false
    },isc.DOMGrid);
    view.addSection({title:"Select Elements",autoShow:true,
                      items:[this.expressionForm,this.selectedNodesView]});
}
,isc.A._wsdlLoaded=function isc_DSWizard__wsdlLoaded(service){
    this.service=service;
    this.serviceInput.setDataSource(this.service.getInputDS(this.operationName));
}
,isc.A.callService=function isc_DSWizard_callService(){
    if(!this.serviceInput.validate())return;
    var inputDS=this.serviceInput.dataSource,
        criteria=this.serviceInput.getValuesAsCriteria(),
        serviceInputs=this.serviceInputs=inputDS.getServiceInputs({data:criteria});
    if(this.requestEditor){
        if(this.requestEditor.getValue("useEditedMessage")){
            var requestBody=this.requestEditor.getValue("requestBody");
            serviceInputs.requestBody=requestBody;
        }else{
            this.requestEditor.setValue("requestBody",serviceInputs.requestBody);
        }
    }
    serviceInputs.callback=
        this.getID()+".serviceOutput.setRootElement(xmlDoc.documentElement)";
    isc.xml.getXMLResponse(serviceInputs);
}
,isc.A.selectNodes=function isc_DSWizard_selectNodes(){
    var expressionForm=this.expressionForm,
        sourceDoc=this.serviceOutput.rootElement,
        selectedNodes;
    this.selectBy=expressionForm.getValue("selectBy");
    if(this.selectBy=="xpath"){
        this.recordName=null;
        this.recordXPath=expressionForm.getValue("expression");
        selectedNodes=isc.xml.selectNodes(sourceDoc,this.recordXPath);
    }else{
        this.recordXPath=null;
        this.recordName=expressionForm.getValue("expression");
        var nodeList=sourceDoc.getElementsByTagName(this.recordName);
        selectedNodes=[];
        for(var i=0;i<nodeList.length;i++)selectedNodes.add(nodeList[i]);
    }
    this.selectedNodesView.setRootElement({childNodes:selectedNodes});
    this.selectedNodes=selectedNodes;
    this.nextButton.enable();
}
,isc.A.enterBindingPage=function isc_DSWizard_enterBindingPage(page){
    var sampleData=this.selectedNodesView.data,
        sampleNode=sampleData.get(0)._element,
        nodeType=sampleNode.getAttribute("xsi:type")||sampleNode.tagName;
    if(nodeType.contains(":"))nodeType=nodeType.substring(nodeType.indexOf(":")+1);
    var ds=this.outputDS=isc.DS.get(nodeType);
    this.logWarn("nodeType is: "+nodeType+", ds is: "+ds);
    this.boundGrid=this.createAutoChild("boundGrid",{
        dataSource:ds,
        data:this.selectedNodes,
        alternateRecordStyles:true
    },isc.ListGrid)
    page.view=this.boundGrid;
}
,isc.A.enterKapowPickRobotPage=function isc_DSWizard_enterKapowPickRobotPage(page){
    if(!this.kapowRobotList){
        this.kapowRobotList=this.createAutoChild("kapowRobotList",{
            selectionChanged:function(){
                var hasSelection=this.getSelectedRecord()!=null;
                this.creator.nextButton.setDisabled(!hasSelection);
            }
        },isc.ListGrid);
        page.view=this.kapowRobotList;
    }
    var kapowRobotListDS=isc.XJSONDataSource.create({
        ID:"kapowRobotListDS",
        callbackParam:"json.callback",
        dataURL:window.robotServerURL+"/ISCVBListAllRobots?format=JSON",
        fields:[
            {name:"name",title:"Robot"},
            {name:"type",title:"Type"}
        ],
        transformResponse:function(dsResponse){
            var data=[];
            for(var i=0;i<dsResponse.data.length;i++){
                var robot=dsResponse.data[i];
                if(robot.name.startsWith("ISCVB"))continue;
                data.add(robot);
            }
            dsResponse.data=data;
            dsResponse.totalRows=dsResponse.data.length;
            dsResponse.endRow=dsResponse.data.length-1;
            return dsResponse;
        }
    });
    this.kapowRobotList.setDataSource(kapowRobotListDS);
    this.kapowRobotList.fetchData();
}
,isc.A.kapowFinish=function isc_DSWizard_kapowFinish(){
    var robots=this.kapowRobotList.getSelection(),
        robotsLength=(robots==null?0:robots.length);
    for(var i=0;i<robotsLength;++i){
        var robot=robots[i];
        isc.XMLTools.loadXML(window.robotServerURL+"/admin/"+robot.name+".robot",this.getID()+".kapowRobotLoaded(xmlDoc,'"+robot.name+"','"+robot.type+"')");
    }
}
,isc.A.saveDataSource=function isc_DSWizard_saveDataSource(ds){
    var dsClass=ds.getClassName();
    var schema;
    if(isc.DS.isRegistered(dsClass)){
        schema=isc.DS.get(dsClass);
    }else{
        schema=isc.DS.get("DataSource");
        ds._constructor=dsClass;
    }
    var xml=schema.xmlSerialize(ds);
    this.logWarn("saving DS with XML: "+xml);
    isc.DMI.callBuiltin({
        methodName:"saveSharedXML",
        arguments:[
            "DS",
            ds.ID,
            xml
        ]
    });
}
,isc.A.kapowRobotLoaded=function isc_DSWizard_kapowRobotLoaded(xmlDoc,robotName,robotType){
    this.logInfo("loaded robot: "+robotName);
    var outputs=isc.xml.selectNodes(xmlDoc,"//property[@name='startModelObjects']/element[@class='kapow.robot.common.domain.Entity']/property");
    outputs=isc.xml.toJS(outputs);
    var outputFields=[];
    for(var i=0;i<outputs.length;i++){
        var prop=outputs[i];
        if(!prop.xmlTextContent)continue;
        outputFields.add({
            name:prop.xmlTextContent,
            type:this.fieldTypeForJavaClass(prop["class"])
        });
    }
    this.logWarn("Robot: "+robotName+" - derived outputFields: "+isc.echoAll(outputFields));
    var outputDS;
    if(outputFields.length){
        outputDS=isc.DataSource.create({
            ID:robotName+"DS",
            callbackParam:"json.callback",
            dataURL:window.robotServerURL+"/"+robotName+"?format=JSON",
            noAutoFetch:true,
            fields:outputFields,
            dataFormat:"json",
            dataTransport:"scriptInclude"
        });
    }else if(robotType=="rss"){
        var outputDS=isc.DataSource.create({
            ID:robotName+"DS",
            dataURL:window.robotServerURL+"/"+robotName,
            recordXPath:"//default:item",
            noAutoFetch:true,
            fields:[
                {name:"title"},
                {name:"link",type:"link"},
                {name:"description"},
                {name:"created"},
                {name:"category"},
                {name:"email"},
                {name:"name"},
                {name:"rights"}
            ]
        });
    }
    if(outputDS){
        this.callingBuilder.addDataSource(outputDS);
        this.saveDataSource(outputDS);
    }
    var inputs=isc.xml.selectNodes(xmlDoc,"//property[@name='queryParameters']/element[@class='kapow.robot.common.domain.Entity']/property");
    inputs=isc.xml.toJS(inputs);
    var inputFields=[];
    for(var i=0;i<inputs.length;i++){
        var prop=inputs[i];
        if(!prop.xmlTextContent)continue;
        if(prop.name&&prop.name.startsWith("value"))continue;
        inputFields.add({
            name:prop.xmlTextContent,
            type:this.fieldTypeForJavaClass(prop["class"])
        });
    }
    this.logWarn("Robot: "+robotName+" - derived inputFields: "+isc.echoAll(inputFields));
    if(inputFields.length){
        var inputDS=isc.DataSource.create({
            ID:robotName+"InputDS",
            type:"generic",
            fields:inputFields
        });
        this.callingBuilder.addDataSource(inputDS);
        this.saveDataSource(inputDS);
    }
    if(this.callingBuilder)this.callingBuilder.wizardWindow.hide();
    this.resetWizard();
}
,isc.A.fieldTypeForJavaClass=function isc_DSWizard_fieldTypeForJavaClass(c){
    switch(c){
        case"java.lang.Boolean":
            return"boolean";
        case"java.util.Date":
            return"date";
        case"java.lang.Byte":
        case"java.lang.Short":
        case"java.lang.Integer":
        case"java.lang.Long":
        case"java.lang.BigInteger":
            return"integer";
        case"java.lang.Float":
        case"java.lang.Double":
        case"java.lang.BigDecimal":
            return"float";
        default:
            return"text";
    }
}
,isc.A.enterSFPickEntityPage=function isc_DSWizard_enterSFPickEntityPage(page){
    this.sfService=isc.WebService.get("urn:partner.soap.sforce.com");
    if(!this.sfEntityList){
        this.sfEntityList=this.createAutoChild("sfEntityList",{
            fields:[{name:"objectType",title:"Object Type"}],
            selectionChanged:function(){
                var hasSelection=this.getSelectedRecord()!=null;
                this.creator.nextButton.setDisabled(!hasSelection);
            }
        },isc.ListGrid);
        page.view=this.sfEntityList;
    }
    this.sfService.getEntityList({target:this,methodName:"getEntityListReply"});
}
,isc.A.getEntityListReply=function isc_DSWizard_getEntityListReply(list){
    var objects=[];
    for(var i=0;i<list.length;i++){
        objects.add({objectType:list[i]});
    }
    this.sfEntityList.setData(objects);
}
,isc.A.enterSFDonePage=function isc_DSWizard_enterSFDonePage(page){
    var objectType=this.sfEntityList.getSelectedRecord().objectType;
    if(!this.sfGrid){
        this.sfGrid=this.createAutoChild("sfGrid",{
        },isc.ListGrid);
    }
    this.sfService.getEntity(objectType,{target:this,methodName:"showSFBoundGrid"});
    page.view=this.sfGrid;
}
,isc.A.showSFBoundGrid=function isc_DSWizard_showSFBoundGrid(schema){
    this.sfGrid.setDataSource(schema);
    this.sfGrid.fetchData();
}
,isc.A.sfFinish=function isc_DSWizard_sfFinish(){
    this.showDSEditor(this.sfGrid.dataSource,true,
                      "You can remove fields below to prevent them from being shown, "+
                      "and alter user-visible titles.");
}
,isc.A.finish=function isc_DSWizard_finish(){
    if(this.getCurrentPage().ID=="SFDonePage")return this.sfFinish();
    if(this.getCurrentPage().ID=="KapowPickRobotPage")return this.kapowFinish();
    this.logWarn("passing output DS: "+this.echo(this.outputDS));
    var ds=this.service.getFetchDS(this.operationName,this.outputDS);
    ds.recordXPath=this.recordXPath;
    ds.recordName=this.recordName;
    ds.fetchSchema.defaultCriteria=isc.addProperties({},this.serviceInput.getValues());
    this.logWarn("created DataSource with props: "+this.echo(ds));
    this.showDSEditor(ds);
}
,isc.A.showDSEditor=function isc_DSWizard_showDSEditor(ds,isNew,instructions){
    this.callingBuilder.showDSEditor(ds,isNew,instructions);
    this.callingBuilder.wizardWindow.hide();
    this.resetWizard();
}
,isc.A.closeClick=function isc_DSWizard_closeClick(){
    this.Super("closeClick",arguments);
    this.resetWizard();
}
,isc.A.resetWizard=function isc_DSWizard_resetWizard(){
    if(this.dsTypePicker)this.dsTypePicker.clearValues();
    if(this.servicePicker&&this.servicePicker.selection){
        this.servicePicker.selection.deselectAll();
        this.servicePicker.fireSelectionUpdated();
    }
    if(this.operationPicker)this.operationPicker.setData([]);
    if(this.callServicePage){
        this.serviceInput.clearValues();
        this.serviceOutput.setData([]);
        this.expressionForm.clearValues();
        this.selectedNodesView.setData([]);
    }
    this.Super("resetWizard",arguments);
}
,isc.A.startJavaBeanWizard=function isc_DSWizard_startJavaBeanWizard(wizard,record){
    isc.askForValue("Enter the name of the JavaBean for which you want to generate a DataSource.",
        function(value){
            wizard.continueJavaBeanWizard(wizard,record,value);
        },{width:400}
    );
}
,isc.A.continueJavaBeanWizard=function isc_DSWizard_continueJavaBeanWizard(wizard,record,value){
    if(value){
        wizard.getJavaBeanDSConfig(wizard,record,value);
    }
}
,isc.A.getJavaBeanDSConfig=function isc_DSWizard_getJavaBeanDSConfig(wizard,record,className){
    if(className!=null){
        isc.DMI.call("isc_builtin","com.isomorphic.tools.BuiltinRPC",
            "getDataSourceConfigFromJavaClass",
            className,
            function(data){
                wizard.finishJavaBeanWizard(wizard,record,className,data)
            }
        );
    }
}
,isc.A.finishJavaBeanWizard=function isc_DSWizard_finishJavaBeanWizard(wizard,record,className,response){
    var config=response.data.dsConfig?response.data.dsConfig:null;
    if(isc.isAn.Object(config)){
        if(record.wizardDefaults)isc.addProperties(config,record.wizardDefaults);
        wizard.showDSEditor(config,true);
    }else{
        isc.say(config);
    }
}
);
isc.B._maxIndex=isc.C+31;

isc.defineClass("SchemaViewer","VLayout");
isc.A=isc.SchemaViewer;
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.B.push(isc.A.getTreeFromService=function isc_c_SchemaViewer_getTreeFromService(service){
    return isc.Tree.create({
        service:service,
        nameProperty:"_nodeName",
        titleProperty:"name",
        loadChildren:function(parent){
            if(this.isLoaded(parent))return;
            if(parent==this.root&&isc.isA.WebService(this.service)){
                var operations=this.service.getOperations();
                operations.setProperty("type","Operation");
                this.addList(operations,parent);
            }else if(parent==this.root&&isc.isA.SchemaSet(this.service)){
                var schemaSet=this.service;
                for(var i=0;i<schemaSet.schema.length;i++){
                    this.add(this.getSchemaNode(schemaSet.schema[i]),
                             this.root);
                }
            }else if(parent.inputMessage){
                var message=this.getMessageNode(parent,true);
                if(message!=null)this.add(message,parent);
                message=this.getMessageNode(parent,false);
                if(message!=null)this.add(message,parent);
            }else if(parent.isComplexType){
                var parentDS=parent.liveSchema;
                for(var fieldName in parentDS.getFields()){
                    var field=parentDS.getField(fieldName);
                    if(!parentDS.fieldIsComplexType(fieldName)){
                        this.add(isc.addProperties({},field),parent);
                    }else{
                        var childDS=parentDS.getSchema(field.type);
                        var node=this.getSchemaNode(childDS,field.name,field.xmlMaxOccurs);
                        this.add(node,parent);
                    }
                }
            }
            this.setLoadState(parent,isc.Tree.LOADED);
        },
        isFolder:function(node){
            return(node==this.root||node.inputMessage||node.isComplexType);
        },
        getSchemaNode:function(childDS,fieldName,maxOccurs){
            var schemaSet=isc.SchemaSet.get(childDS.schemaNamespace),
                field=childDS.getField(fieldName),
                node={
                name:fieldName||childDS.tagName||childDS.ID,
                type:childDS.ID,
                isComplexType:true,
                xmlMaxOccurs:maxOccurs,
                liveSchema:childDS,
                namespace:childDS.schemaNamespace,
                location:schemaSet?schemaSet.location:null
            };
            return node;
        },
        getMessageNode:function(operation,isInput){
            var messageDS=isInput?this.service.getRequestMessage(operation):
                                      this.service.getResponseMessage(operation);
            if(!messageDS)return;
            return{
                name:messageDS.ID,
                type:messageDS.ID,
                isComplexType:true,
                liveSchema:messageDS
            };
        }
    });
}
);
isc.B._maxIndex=isc.C+1;

isc.A=isc.SchemaViewer.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.showTestUI=true;
isc.A.operationIcon="[SKINIMG]/SchemaViewer/operation.png";
isc.A.complexTypeIcon="[SKINIMG]/SchemaViewer/complexType.gif";
isc.A.simpleTypeIcon="[SKINIMG]/SchemaViewer/simpleType.png";
isc.B.push(isc.A.setWsdlURL=function isc_SchemaViewer_setWsdlURL(url){
    this.wsdlURL=url;
    this.urlForm.setValue("url",url);
}
,isc.A.getWsdlURLs=function isc_SchemaViewer_getWsdlURLs(){
    var loadedServiceURNs=isc.WebService.services.getProperty("serviceNamespace"),
        defaultWSDLs=this.wsdlURLs;
    if(defaultWSDLs==null&&loadedServiceURNs.length==0)return;
    if(defaultWSDLs==null)defaultWSDLs=[];
    defaultWSDLs.addList(loadedServiceURNs);
    return defaultWSDLs;
}
,isc.A.initWidget=function isc_SchemaViewer_initWidget(){
    this.Super("initWidget",arguments);
    this.createChildren();
}
,isc.A.createChildren=function isc_SchemaViewer_createChildren(){
    var wsdlURLs=this.getWsdlURLs();
    this.addAutoChild("urlForm",{
        numCols:4,
        colWidths:[100,"*",100,100],
        itemHoverWidth:300,
        saveOnEnter:true,
        saveData:function(){
            this.creator.fetchSchema();
        },
        items:[
            {name:"url",title:"WSDL",width:"*",defaultValue:this.wsdlURL,
                editorType:(wsdlURLs!=null?"ComboBoxItem":"TextItem"),
                autoComplete:(wsdlURLs!=null?"smart":null),
                showAllOptions:true,textMatchStyle:"substring",
                valueMap:wsdlURLs
            },
            {type:"submit",title:"Show Messages",
              startRow:false,colSpan:1,endRow:false,width:"*"
            },
            {showTitle:false,startRow:false,width:"*",
              formItemType:"pickTree",
              shouldSaveValue:false,
              buttonProperties:{
                unselectedTitle:"Download",
                itemSelected:function(item){
                    this.canvasItem.form.creator.download(item.name);
                    return false;
                }
              },
              valueTree:isc.Tree.create({
                  root:{name:"download",title:"Download",children:[
                          {name:"js",title:"as JS"},
                          {name:"xml",title:"as XML"}
                        ]}
              }),
              icons:[
                {src:"[SKIN]/actions/help.png",width:16,height:16,
                  prompt:"You can use the <b>Download</b> feature to download a SmartClient"
                         +" WebService definition for the specified WSDL file in either XML"
                         +" or JS format.  <p>You can achieve the same result by calling"
                         +" <i>XMLTools.loadWSDL()</i> or by using the <code>&lt;isomorphic"
                         +":loadWSDL&gt;</code> JSP tag, however, for non-Java backends or"
                         +" for production use, a .js file should be obtained from this"
                         +" interface and loaded via &lt;SCRIPT SRC=&gt; either individually"
                         +" or combined with other files.  <p>See the reference documentation"
                         +" for details.",
                  click:"isc.say(this.prompt)"
                }
              ]
            }
        ]
    },isc.DynamicForm);
    this.addMember(isc.VLayout.create({
        autoDraw:false,
        members:[
            isc.HLayout.create({
                autoDraw:false,
                members:[
                    this.addAutoChild("treeGrid",{
                        fields:[
                            {treeField:true},
                            {name:"type",title:"Type",width:140},
                            {name:"xmlMaxOccurs",title:"#",width:35},
                            {name:"namespace",title:"NS",width:35,showHover:true,
                             hoverHTML:function(record,value){return"<NOBR>"+value+"<NOBR>"}},
                            {name:"location",title:"URL",width:35,showHover:true,
                             hoverHTML:function(record,value){return"<NOBR>"+value+"<NOBR>"},
                             recordClick:function(viewer,record){
                                 viewer.creator.setWsdlURL(record.location);
                                 viewer.creator.fetchSchema();
                             }
                            }
                        ],
                        nodeClick:function(grid,node,rowNum){
                            if(this.creator.showTestUI){
                                this.creator.updateInputStack(node);
                            }
                        },
                        getIcon:function(node){
                            if(node.type=="Operation")return this.creator.operationIcon;
                            else if(node.isComplexType)return this.creator.complexTypeIcon;
                            else return this.creator.simpleTypeIcon;
                        },
                        showResizeBar:true
                    },isc.TreeGrid),
                    isc.VLayout.create({
                        visibility:(this.showTestUI?"inherit":"hidden"),
                        members:[
                            this.addAutoChild("inputStack",{
                                overflow:"auto",
                                visibilityMode:"multiple",
                                autoDraw:false,
                                sections:[
                                    {showHeader:true,title:"Input Message (Body)",
                                     items:[
                                        this.addAutoChild(
                                            "inputBodyForm",
                                            {useFlatFields:true},
                                            isc.DynamicForm)
                                     ]
                                    }
                                ]
                            },isc.SectionStack),
                            isc.IButton.create({
                                creator:this,
                                autoDraw:false,
                                title:"Invoke",
                                click:function(){
                                    this.creator.updateResponseTree();
                                }
                            })
                        ]
                    })
                ]
            }),
            this.addAutoChild("responseStack",{
                visibility:(this.showTestUI?"inherit":"hidden"),
                autoDraw:false,
                visibilityMode:"multiple",
                sections:[
                    this.getResponseSectionConfig()
                ]
            },
            isc.SectionStack)
        ]
    })
    );
}
,isc.A.download=function isc_SchemaViewer_download(format){
    var url=this.urlForm.getValue("url");
    if(!url){
        isc.warn("Please type in a WSDL URL");
        return;
    }
    var fileName=url.replace(/(.*\/)?(.*)/,"$2")
                      .replace(/(.*?)\?.*/,"$1")
                      .replace(/(.*)\..*/,"$1")
                   +"."+format;
    isc.DMI.callBuiltin({
        methodName:"downloadWSDL",
        arguments:[url,format,fileName],
        requestParams:{
            showPrompt:false,
            useXmlHttpRequest:false,
            timeout:0
        }
    });
}
,isc.A.fetchSchema=function isc_SchemaViewer_fetchSchema(){
    var url=this.urlForm.getValue("url");
    if(url==null||url=="")return;
    if(isc.WebService.get(url))return this.fetchSchemaReply(isc.WebService.get(url));
    isc.RPCManager.addClassProperties({
        defaultPrompt:"Loading WSDL Schema",
        showPrompt:true
    })
    isc.xml.loadWSDL(url,{target:this,methodName:"fetchSchemaReply"},null,true,
                     {captureXML:true});
}
,isc.A.fetchSchemaReply=function isc_SchemaViewer_fetchSchemaReply(service){
    isc.RPCManager.addClassProperties({
        defaultPrompt:"Contacting Server..."
    });
    this.service=service;
    delete this.operationName;
    var theTree=isc.SchemaViewer.getTreeFromService(service);
    this.treeGrid.setData(theTree);
    this.clearInputStack();
    this.clearResponseTree();
}
,isc.A.clearInputStack=function isc_SchemaViewer_clearInputStack(){
    var stack=this.inputStack,
        sectionsArr=stack.sections.duplicate(),
        headerSections=[];
    for(var i=0;i<sectionsArr.length;i++){
        if(sectionsArr[i].isHeaderSection)stack.removeSection(sectionsArr[i]);
    }
    this.inputBodyForm.hide();
    this.inputBodyForm.clearValues();
}
,isc.A.updateInputStack=function isc_SchemaViewer_updateInputStack(node){
    this.clearInputStack();
    var operationNode=node;
    while(operationNode.type!="Operation"){
        operationNode=this.treeGrid.data.getParent(operationNode);
    }
    if(!operationNode)return;
    var operationName=operationNode.name;
    this.operationName=operationName;
    var inputHeaderSchema=this.service.getInputHeaderSchema(operationName);
    if(inputHeaderSchema!=null){
        var index=0;
        for(var schemaName in inputHeaderSchema){
            var schema=inputHeaderSchema[schemaName],
                editForm;
            if(isc.isA.DataSource(schema)){
                editForm=isc.DynamicForm.create({
                    useFlatFields:true,
                    dataSource:schema
                })
            }else{
                editForm=isc.DynamicForm.create({
                    _singleField:true,
                    fields:[schema]
                })
            }
            this.inputStack.addSection({showHeader:true,isHeaderSection:true,
                              schemaName:schemaName,
                              title:"Header: "+schemaName,
                              items:[editForm]
            },index);
            index+=1;
        }
    }
    var inputDS=this.service.getInputDS(operationName);
    this.inputBodyForm.setDataSource(inputDS);
    if(!this.inputBodyForm.isVisible())this.inputBodyForm.show();
}
,isc.A.updateResponseTree=function isc_SchemaViewer_updateResponseTree(){
    if(this.operationName==null)return;
    var params=this.inputBodyForm.getValues(),
        headerParams,
        service=this.service;
    for(var i=0;i<this.inputStack.sections.length;i++){
        var section=this.inputStack.sections[i];
        if(!section.isHeaderSection)continue;
        if(headerParams==null)headerParams={};
        var editForm=section.items[0];
        if(editForm._singleField){
            headerParams[section.schemaName]=editForm.getValue(editForm.getItem(0));
        }else{
            headerParams[section.schemaName]=editForm.getValues();
        }
    }
    if(this.logIsDebugEnabled())
        this.logDebug("operation:"+this.operationName+
        ", body params:"+this.echoAll(params)+", headerParams:"+this.echoAll(headerParams));
    service.callOperation(this.operationName,
                            params,null,
                            this.getID()+".setResponseTreeDoc(xmlDoc, rpcResponse, wsRequest)",
                            {willHandleError:true,
                             headerData:headerParams,
                             useFlatFields:true,useFlatHeaderFields:true}
                            );
}
,isc.A.getResponseSectionConfig=function isc_SchemaViewer_getResponseSectionConfig(){
    return{expanded:true,title:"Service Response",
             headerControls:[
                 isc.LayoutSpacer.create(),
                 isc.IButton.create({
                    width:200,
                    title:"Generate Sample Response",
                    creator:this,
                    click:function(){
                        if(!this.creator.operationName)return;
                        var data=this.creator.service.getSampleResponse(this.creator.operationName);
                        data=isc.XMLTools.parseXML(data);
                        this.creator.setResponseTreeDoc(data);
                        this.creator.responseStack.setSectionTitle(0,"Service Response [Generated Sample]");
                        return false;
                    },
                    height:16,layoutAlign:"center",extraSpace:4,autoDraw:false
                 }),
                 isc.IButton.create({
                    width:200,
                    title:"Generate Sample Request",
                    creator:this,
                    click:function(){
                        if(!this.creator.operationName)return;
                        var data=this.creator.service.getSampleRequest(this.creator.operationName);
                        data=isc.XMLTools.parseXML(data);
                        this.creator.showSampleRequest(data);
                        return false;
                    },
                    height:16,layoutAlign:"center",extraSpace:4,autoDraw:false
                 })
             ],
             items:[
             ]
            }
}
,isc.A.setResponseTreeDoc=function isc_SchemaViewer_setResponseTreeDoc(xmlDoc,rpcResponse,wsRequest){
    if(rpcResponse&&rpcResponse.status<0){
        var faultStrings;
        if(rpcResponse.httpResponseCode==500){
            faultStrings=xmlDoc.selectNodes("//faultstring");
            if(faultStrings!=null)faultStrings=isc.XML.toJS(faultStrings);
            if(faultStrings.length==0)faultStrings=null;
        }
        if(faultStrings){
            isc.warn("<b>Server Returned HTTP Code 500 (Internal Error)</b>"
                    +(faultStrings&&faultStrings.length>0?
                        ("<br><br>"+faultStrings.join("<br>")):""));
        }else{
            isc.RPCManager.handleError(rpcResponse,wsRequest);
        }
        return;
    }
    this.logInfo("showing a tree response");
    if(this.logIsDebugEnabled())this.logDebug("response data:"+this.echoAll(xmlDoc));
    this.clearSampleRequest();
    this.xmlDoc=xmlDoc;
    var domTree=isc.DOMTree.create({rootElement:xmlDoc.documentElement});
    if(this.responseTree){
        this.responseTree.setData(domTree);
    }else{
        this.addAutoChild("responseTree",{data:domTree},isc.DOMGrid)
    }
    if(!this.showingResponseTree){
        this.responseStack.removeSection(0);
        this.responseStack.addSection(
            isc.addProperties(
                this.getResponseSectionConfig(),
                {items:[this.responseTree]}
            ),
            0
        );
    }
    this.showingResponseTree=true;
}
,isc.A.clearResponseTree=function isc_SchemaViewer_clearResponseTree(){
    this.clearSampleRequest();
    if(!this.showingResponseTree)return;
    this.responseStack.removeSection(0);
    this.responseStack.addSection(this.getResponseSectionConfig())
    delete this.showingResponseTree;
}
,isc.A.showSampleRequest=function isc_SchemaViewer_showSampleRequest(data){
    this.logInfo("showing a sample request");
    if(this.logIsDebugEnabled())this.logDebug("sample request data:"+this.echoAll(data));
    var domTree=isc.DOMTree.create({rootElement:data.documentElement});
    if(!this.showingSampleRequest){
        this.responseStack.addSection({
            isSampleRequest:true,
            expanded:true,resizable:true,
            title:"Generated Sample Service Request",
            items:[
                this.addAutoChild("requestTree",{data:domTree},isc.DOMGrid)
            ]
        });
    }else{
        this.requestTree.setData(domTree);
    }
    this.showingSampleRequest=true
}
,isc.A.clearSampleRequest=function isc_SchemaViewer_clearSampleRequest(){
    if(this.showingSampleRequest){
        for(var i=0;i<this.responseStack.sections.length;i++){
            if(this.responseStack.sections[i].isSampleRequest){
                this.responseStack.removeSection(i);
                break;
            }
        }
    }delete this.showingSampleRequest;
}
);
isc.B._maxIndex=isc.C+15;

isc.ClassFactory.defineClass("DatabaseBrowser","Window");
isc.A=isc.DatabaseBrowser.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.orientation="vertical";
isc.A.title="Database Browser";
isc.A.width="90%";
isc.A.height="90%";
isc.A.isModal=true;
isc.A.showModalMask=true;
isc.A.canDragResize=true;
isc.A.serverType="sql";
isc.A.schemaTreeConstructor="ListGrid";
isc.A.schemaTreeDefaults={
        autoParent:"schemaView",
        dbBrowser:this.creator,
        dataSource:isc.DataSource.create({
            ID:"_dbBrowserSchemaTreeDS",
            clientOnly:true,
            fields:[
                {name:"name",title:"Name"},
                {name:"type",title:"Type",width:60,valueMap:["table","view"]}
            ]
        }),
        showFilterEditor:true,
        filterOnKeypress:true,
        canExpandRecords:true,
        detailDefaults:{
            _constructor:"ListGrid",
            autoFitData:"vertical",
            autoFitMaxRecords:8,
            showResizeBar:true
        },
        getExpansionComponent:function(record){
            var component=this.createAutoChild("detail",{
                sortField:"primaryKey",
                sortDirection:"descending",
                defaultFields:[
                    {name:"name",title:"Column",formatCellValue:function(value,record){
                        if(record.primaryKey)return"<b>"+value+"</b>";
                        return value;
                    }},
                    {name:"type",title:"Type",width:50},
                    {name:"length",title:"Length",width:45},
                    {name:"primaryKey",title:"PK",type:"boolean",showIf:"false",width:22}
                ]
            });
            isc.DMI.call("isc_builtin","com.isomorphic.tools.BuiltinRPC","getFieldsFromTable",
                record.name,this.schema,this.serverType,this.creator.dbName,
                function(rpcResponse,data){
                component.setData(data);
            });
            return component;
        },
        selectionChanged:function(record,state){
            if(state){
                var objectName=record.name;
                if(objectName&&objectName!=this.creator._selectedTable){
                    this.creator.getDataSourceFromTable(objectName);
                    this.creator.populateDataViewHeader();
                }
            }
        }
    };
isc.A.schemaRefreshButtonDefaults={
        _constructor:"Img",
        size:16,
        src:"[SKIN]/actions/refresh.png",
        click:"this.creator.getDatabaseTables()"
    };
isc.A.databaseListConstructor="ListGrid";
isc.A.databaseListDefaults={
        height:150,
        autoParent:"schemaView",
        dataSource:isc.DataSource.create({
            ID:"_dbBrowserDBListDS",
            clientOnly:true,
            fields:[
                {name:"dbName",title:"Name"},
                {name:"dbStatus",title:"Status"},
                {name:"dbProductName",title:"Product Name"},
                {name:"dbProductVersion",title:"Product Version"}
            ]
        }),
        defaultFields:[
            {name:"dbName"},
            {name:"dbStatus"}
        ],
        sortField:"dbName",
        showFilterEditor:true,
        filterOnKeypress:true,
        canDragSelectText:true,
        selectionChanged:function(record,state){
            if(state){
                this.creator.clearSchemaTree();
                this.creator.dbName=record.dbName;
                this.creator.getDatabaseTables();
            }
        },
        canHover:true,
        cellHoverHTML:function(record){
            if(!this.hoverDV)this.hoverDV=isc.DetailViewer.create({dataSource:this.dataSource,width:200,autoDraw:false});
            this.hoverDV.setData(record);
            return this.hoverDV.getInnerHTML();
        }
    };
isc.A.dbListConfigButtonDefaults={
        _constructor:"Img",
        size:16,
        src:"database_gear.png",
        click:"this.creator.configureDatabases()"
    };
isc.A.dbListRefreshButtonDefaults={
        _constructor:"Img",
        size:16,
        src:"[SKIN]/actions/refresh.png",
        click:"this.creator.getDefinedDatabases()"
    };
isc.A.dataGridConstructor="ListGrid";
isc.A.dataGridDefaults={
        canDragSelectText:true,
        showFilterEditor:true,
        autoFitFieldWidths:true,
        autoFitWidthApproach:"title",
        autoParent:"dataView"
    };
isc.A.showSelectButton=true;
isc.A.selectButtonConstructor="Button";
isc.A.selectButtonDefaults={
        title:"Next >",
        enabled:false,
        autoParent:"outerLayout"
    };
isc.A.outerLayoutDefaults={
         _constructor:isc.VLayout,
         width:"100%",height:"100%",
         autoSize:true,autoDraw:true,
         autoParent:"body"
    };
isc.A.innerLayoutDefaults={
         _constructor:isc.HLayout,
         width:"100%",height:"100%",
         autoDraw:true,
         autoParent:"outerLayout"
    };
isc.A.showSchemaView=true;
isc.A.schemaViewDefaults={
         _constructor:isc.SectionStack,
         visibilityMode:"multiple",
         autoParent:"innerLayout"
    };
isc.A.showDataView=true;
isc.A.dataViewDefaults={
         _constructor:isc.SectionStack,
         width:"65%",height:"100%",
         autoParent:"innerLayout"
    }
;
isc.B.push(isc.A.configureDatabases=function isc_DatabaseBrowser_configureDatabases(){
        var _this=this;
        var dbConsole=isc.DBConfigurator.showWindow({
            width:this.getVisibleWidth()-50,
            height:this.getVisibleHeight()-50,
            autoCenter:true,
            isModal:true,
            closeClick:function(){
                this.destroy();
                _this.getDefinedDatabases();
            }
        });
    }
);
isc.B._maxIndex=isc.C+1;

isc.A=isc.DatabaseBrowser.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.B.push(isc.A.initWidget=function isc_DatabaseBrowser_initWidget(){
    this.Super("initWidget",arguments);
    this.title="Database Browser - "+this.serverType.toUpperCase();
    this.createChildren();
}
,isc.A.createChildren=function isc_DatabaseBrowser_createChildren(){
    this.Super("createChildren");
    this.body.hPolicy="fill";
    this.body.vPolicy="fill";
    this.addAutoChild("outerLayout");
    this.addAutoChild("innerLayout",null,null,this.outerLayout);
    this.addAutoChild("schemaView",{showResizeBar:this.showDataView},null,this.innerLayout);
    this.databaseList=this.createAutoChild("databaseList");
    this.dbListConfigButton=this.createAutoChild("dbListConfigButton");
    this.dbListRefreshButton=this.createAutoChild("dbListRefreshButton");
    if(this.serverType=="sql"){
        this.schemaView.addSection({
            title:"Databases",showHeader:true,expanded:true,hidden:false,
            items:[this.databaseList],
            controls:[this.dbListConfigButton,this.dbListRefreshButton]
        });
    }
    this.addAutoChild("dataView",null,null,this.innerLayout);
    this.dataView.addSection({autoDraw:true,showHeader:true,expanded:true,hidden:false});
    this.dataStack=this.dataView.sections[0];
    this.schemaTree=this.createAutoChild("schemaTree");
    this.schemaRefreshButton=this.createAutoChild("schemaRefreshButton");
    this.schemaView.addSection({
        title:"Tables & Views",
        showHeader:true,expanded:true,hidden:false,
        items:[this.schemaTree],
        controls:[this.schemaRefreshButton]
    });
    var dbBrowser=this;
    this.dataGrid=this.createAutoChild("dataGrid");
    this.dataStack.addItem(this.dataGrid);
    this.outerLayout.addMember(isc.LayoutSpacer.create({height:"10"}));
    this.addAutoChild("selectButton",{
        click:function(){
            dbBrowser.hide();
            dbBrowser._paletteNode.defaults=dbBrowser.getGeneratedDataSourceObject();
            dbBrowser.fireCallback(dbBrowser._getResultsCallback,"node",
                [dbBrowser._paletteNode])
        }
     },null,this.outerLayout);
    this.delayCall("getDefinedDatabases");
}
,isc.A.getDefinedDatabases=function isc_DatabaseBrowser_getDefinedDatabases(){
    if(this.serverType=="hibernate"){
        this.databaseList.hide();
        this.dbName=null;
        this.getDatabaseTables();
    }else{
        isc.DMI.call({
            appID:"isc_builtin",
            className:"com.isomorphic.tools.AdminConsole",
            methodName:"getDefinedDatabases",
            arguments:[true],
            callback:this.getID()+".populateDatabaseList(data)",
            requestParams:{
                showPrompt:true,
                promptStyle:"dialog",
                prompt:"Loading available databases..."
            }
        });
    }
}
,isc.A.getDatabaseTables=function isc_DatabaseBrowser_getDatabaseTables(){
    var dbBrowser=this;
    var includeList=this.includeSubstring;
    if(includeList&&!isc.isAn.Array(includeList))includeList=[includeList];
    var excludeList=this.excludeSubstring;
    if(excludeList&&!isc.isAn.Array(excludeList))excludeList=[excludeList];
    isc.DMI.call({
        appID:"isc_builtin",
        className:"com.isomorphic.tools.BuiltinRPC",
        methodName:"getTables",
        arguments:[this.serverType,this.dbName,true,true,this.catalog,this.schema,
                    includeList,excludeList],
        callback:function(data){
            dbBrowser.populateSchemaTree(data.data);
        },
        requestParams:{
            showPrompt:true,
            promptStyle:"dialog",
            prompt:"Loading schema..."
        }
    });
}
,isc.A.populateDatabaseList=function isc_DatabaseBrowser_populateDatabaseList(data){
    this.databaseList.dataSource.setCacheData(data);
    var crit={dbStatus:"OK"};
    this.databaseList.setFilterEditorCriteria(crit);
    this.databaseList.filterData(crit);
}
,isc.A.clearSchemaTree=function isc_DatabaseBrowser_clearSchemaTree(data){
    this.schemaTree.setData([]);
    this._selectedTable=null;
    this.populateDataViewHeader();
}
,isc.A.populateSchemaTree=function isc_DatabaseBrowser_populateSchemaTree(data){
    for(var i=0;i<data.length;i++){
        data[i].name=data[i].TABLE_NAME;
        data[i].type=data[i].TABLE_TYPE.toLowerCase();
        data[i].isFolder=true;
        data[i].customIcon="[SKIN]../DatabaseBrowser/data.png";
    }
    this.schemaTree.dataSource.setCacheData(data);
    this.schemaTree.filterData();
    if(this.schemaTreeTitle){
        this.populateSchemaTreeHeader();
    }
    this.tablesRetrieved=true;
}
,isc.A.populateSchemaTreeHeader=function isc_DatabaseBrowser_populateSchemaTreeHeader(){
}
,isc.A.populateDataViewHeader=function isc_DatabaseBrowser_populateDataViewHeader(){
    if(this._selectedTable){
        this.dataGridTitle="Data from table "+this._selectedTable;
        this.dataGrid.setShowHeader(true);
    }else{
        this.dataGridTitle="No table selected";
        this.dataGrid.setDataSource(null);
        this.dataGrid.setFields([{name:"placeholder",title:" "}]);
    }
    this.dataStack.setTitle(this.dataGridTitle);
}
,isc.A.getDataSourceFromTable=function(tableName){

    var dbBrowser=this;
    dbBrowser._selectedTable=tableName;
    dbBrowser.selectButton.setDisabled(false);
    isc.DMI.call("isc_builtin","com.isomorphic.tools.BuiltinRPC","getDataSourceJSONFromTable",
        tableName,this.serverType,this.dbName,tableName+"_dbBrowser",
        function(rpcResponse,data){
            var temp="dbBrowser.generatedDataSourceObject = "+data;
            eval(temp);
            var gdsoFields=dbBrowser.generatedDataSourceObject.fields,
                originalFieldsCopy=[];
            for(var i=0;i<gdsoFields.length;i++){
                originalFieldsCopy[i]=isc.addProperties({},gdsoFields[i]);
            }
            dbBrowser.generatedDataSource=isc.DataSource.create(dbBrowser.generatedDataSourceObject);
            dbBrowser.generatedDataSourceObject.fields=originalFieldsCopy;
            if(dbBrowser.showDataView){
                dbBrowser.dataGrid.setDataSource(dbBrowser.generatedDataSource);
                dbBrowser.dataGrid.fetchData();
            }
        });
}
,isc.A.getGeneratedDataSource=function isc_DatabaseBrowser_getGeneratedDataSource(){
    return this.generatedDataSource;
}
,isc.A.getGeneratedDataSourceObject=function isc_DatabaseBrowser_getGeneratedDataSourceObject(){
    return this.generatedDataSourceObject;
}
,isc.A.getResults=function isc_DatabaseBrowser_getResults(newNode,callback,palette){
    this._getResultsCallback=callback;
    this._paletteNode=newNode;
}
);
isc.B._maxIndex=isc.C+13;

isc.ClassFactory.defineClass("HibernateBrowser","Window");
isc.A=isc.HibernateBrowser.getPrototype();
isc.A.orientation="vertical";
isc.A.width="90%";
isc.A.height="90%";
isc.A.isModal=true;
isc.A.showModalMask=true;
isc.A.canDragResize=true;
isc.A.showMappingTree=true;
isc.A.mappingTreeConstructor="TreeGrid";
isc.A.mappingTreeDefaults={
        autoParent:"mappingView",
        showConnectors:true,
        showOpenIcons:false,
        showDropIcons:false,
        customIconProperty:"customIcon",
        fields:[
            {name:"name",title:"Name",width:"60%",showHover:true},
            {name:"type",title:"Type"},
            {name:"primaryKey",title:"PK",type:"boolean",width:"10%"},
            {name:"length",title:"Length",type:"number"}
        ],
        selectionChanged:function(record,state){
            if(state){
                var objectName=this.data.getLevel(record)==1?record.name:
                    this.data.getParent(record).name;
                if(objectName&&objectName!=this.creator._selectedEntity){
                    this.creator.getDataSourceFromMapping(objectName);
                    this.creator.populateDataViewHeader();
                }
            }
        },
        openFolder:function(node){
            if(this.data.getLevel(node)>1){
                return this.Super("openFolder",arguments);
            }
            this.Super("openFolder",arguments);
            var mappingTree=this;
            var className=node.name;
            isc.DMI.call("isc_builtin","com.isomorphic.tools.BuiltinRPC","getBeanFields",
                className,
                function(data){
                    mappingTree.populateFields(node,data.data);
                }
            );
        },
        getValueIcon:function(field,value,record){
            if(record.type=="entity"){
                return null;
            }else{
                return this.Super("getValueIcon",arguments);
            }
        },
        populateFields:function(node,paramData){
            var data=isc.clone(paramData)
            node.children=[];
            for(var i=0;i<data.length;i++){
                data[i].children=[];
                data[i].customIcon="[SKIN]../DatabaseBrowser/column.png";
            }
            this.data.addList(data,node);
        }
    };
isc.A.dataGridConstructor="ListGrid";
isc.A.dataGridDefaults={
    };
isc.A.title="Hibernate Browser";
isc.A.showSelectButton=true;
isc.A.selectButtonConstructor="Button";
isc.A.selectButtonDefaults={
        title:"Next >",
        enabled:false,
        autoParent:"outerLayout"
    };
isc.A.outerLayoutDefaults={
         _constructor:isc.VLayout,
         width:"100%",height:"100%",
         autoSize:true,autoDraw:true,
         autoParent:"body"
    };
isc.A.innerLayoutDefaults={
         _constructor:isc.HLayout,
         width:"100%",height:"100%",
         autoDraw:true,
         autoParent:"outerLayout"
    };
isc.A.showMappingView=true;
isc.A.mappingViewDefaults={
         _constructor:isc.SectionStack,
         autoParent:"innerLayout"
    };
isc.A.showDataView=true;
isc.A.dataViewDefaults={
         _constructor:isc.SectionStack,
         width:"65%",height:"100%",
         autoParent:"innerLayout"
    }
;

isc.A=isc.HibernateBrowser.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.B.push(isc.A.initWidget=function isc_HibernateBrowser_initWidget(){
    this.Super("initWidget",arguments);
    this.createChildren();
}
,isc.A.createChildren=function isc_HibernateBrowser_createChildren(){
    this.Super("createChildren");
    this.body.hPolicy="fill";
    this.body.vPolicy="fill";
    var hbBrowser=this;
    this.addAutoChild("outerLayout");
    this.addAutoChild("innerLayout",null,null,this.outerLayout);
    this.addAutoChild("mappingView",{showResizeBar:this.showDataView,
        title:"Hibernate Mappings"},null,this.innerLayout);
    this.mappingView.addSection({autoDraw:true,showHeader:true,expanded:true,
        hidden:false,title:"Hibernate Mappings"});
    this.mappingStack=this.mappingView.sections[0];
    this.addAutoChild("dataView",null,null,this.innerLayout);
    this.dataView.addSection({autoDraw:true,showHeader:true,expanded:true,hidden:false});
    this.dataStack=this.dataView.sections[0];
    this.mappingTree=this.createAutoChild("mappingTree");
    this.mappingStack.addItem(this.mappingTree);
    var includeList=this.includeSubstring;
    if(includeList&&!isc.isAn.Array(includeList))includeList=[includeList];
    var excludeList=this.excludeSubstring;
    if(excludeList&&!isc.isAn.Array(excludeList))excludeList=[excludeList];
    isc.DMI.call("isc_builtin","com.isomorphic.tools.BuiltinRPC","getHibernateBeans",
        includeList,excludeList,
        true,
        function(data){
            hbBrowser.populateMappingTree(data.data);
        }
    );
    this.dataGrid=this.createAutoChild("dataGrid");
    this.dataStack.addItem(this.dataGrid);
    this.outerLayout.addMember(isc.LayoutSpacer.create({height:"10"}));
    this.addAutoChild("selectButton",{
        click:function(){
            hbBrowser.hide();
            hbBrowser._paletteNode.defaults=hbBrowser.getGeneratedDataSourceObject();
            hbBrowser.fireCallback(hbBrowser._getResultsCallback,"node",
                [hbBrowser._paletteNode])
        }
     },null,this.outerLayout);
}
,isc.A.populateMappingTree=function isc_HibernateBrowser_populateMappingTree(data){
    for(var i=0;i<data.length;i++){
        data[i].name=data[i].entityName;
        data[i].type="entity";
        data[i].isFolder=true;
        data[i].customIcon="[SKIN]../DatabaseBrowser/data.png"
    }
    this.mappingTree.setData(isc.Tree.create({
        modelType:"children",
        root:{children:data}
    }));
    if(data.length==0){
        this.populateMappingTreeHeader("No Hibernate entities configured");
    }
    this.tablesRetrieved=true;
}
,isc.A.populateMappingTreeHeader=function isc_HibernateBrowser_populateMappingTreeHeader(headerText){
    this.mappingStack.setTitle(headerText);
}
,isc.A.populateDataViewHeader=function isc_HibernateBrowser_populateDataViewHeader(){
    this.dataGridTitle="Data from entity "+this._selectedEntity;
    this.dataStack.setTitle(this.dataGridTitle);
}
,isc.A.getDataSourceFromMapping=function(entityName){

    var hbBrowser=this;
    hbBrowser._selectedEntity=entityName;
    hbBrowser.selectButton.setEnabled(true);
    isc.DMI.call("isc_builtin","com.isomorphic.tools.BuiltinRPC","getDataSourceJSONFromHibernateMapping",
        entityName,entityName+"-hibernateBrowser",
        function(rpcResponse,data){
            var temp="hbBrowser.generatedDataSourceObject = "+data;
            eval(temp);
            hbBrowser.generatedDataSource=isc.DataSource.create(hbBrowser.generatedDataSourceObject);
            if(hbBrowser.showDataView){
                hbBrowser.dataGrid.setDataSource(hbBrowser.generatedDataSource);
                hbBrowser.dataGrid.fetchData();
            }
        });
}
,isc.A.getGeneratedDataSource=function isc_HibernateBrowser_getGeneratedDataSource(){
    return this.generatedDataSource;
}
,isc.A.getGeneratedDataSourceObject=function isc_HibernateBrowser_getGeneratedDataSourceObject(){
    return this.generatedDataSourceObject;
}
,isc.A.getResults=function isc_HibernateBrowser_getResults(newNode,callback,palette){
    this._getResultsCallback=callback;
    this._paletteNode=newNode;
}
);
isc.B._maxIndex=isc.C+9;

isc.defineClass("SelectionOutline","Class");
isc.A=isc.SelectionOutline;
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.flashBorder="1px dashed white";
isc.A.flashCount=7;
isc.A.flashInterval=300;
isc.A.showLabel=true;
isc.A.labelSnapTo="TL";
isc.A.labelSnapEdge="BL";
isc.A.labelSnapOffset=-2;
isc.A.labelOpacity=100;
isc.A._dragHandleHeight=18;
isc.A._dragHandleWidth=18;
isc.A._dragHandleXOffset=-18;
isc.A._dragHandleYOffset=0;
isc.A.border="1px dashed #44ff44";
isc.A.labelBackgroundColor="#44ff44";
isc.B.push(isc.A.setBorder=function isc_c_SelectionOutline_setBorder(border){
        this.border=border;
    }
,isc.A.getBorder=function isc_c_SelectionOutline_getBorder(){
        return this.border;
    }
,isc.A.setLabelBackgroundColor=function isc_c_SelectionOutline_setLabelBackgroundColor(color){
        this.labelBackgroundColor=color;
    }
,isc.A.getLabelBackgroundColor=function isc_c_SelectionOutline_getLabelBackgroundColor(){
        return this.labelBackgroundColor;
    }
,isc.A.select=function isc_c_SelectionOutline_select(name,flash,showLabel,label,resizeFrom){
        var object=name;
        if(isc.isA.String(name))object=window[name];
        if(!isc.isA.Canvas(object)&&!isc.isA.FormItem(object)){
            this.logInfo("Cannot hilite "+name+" - it is neither a Canvas nor a FormItem");
            return;
        }
        if(showLabel==null)showLabel=true;
        if(!label&&(showLabel||(showLabel==null&&this.showLabel))){
            label="<b>"+object.toString()+"</b>";
        }
        if(object==this._object&&label==this._labelText&&
                ((showLabel&&this._showingLabel)||(!showLabel&&!this._showingLabel)))
        {
            if(!this._visible)this.showOutline();
            return;
        }
        this.logInfo("Selection changing from "+this._object+" to "+object,
                       "selectionOutline");
        this.deselect();
        if(!this._outline){
            this._createOutline(object,label,showLabel);
        }else{
            this._outline.top.setBorder(this.border);
            this._outline.left.setBorder(this.border);
            this._outline.bottom.setBorder(this.border);
            this._outline.right.setBorder(this.border);
        }
        this._outline.top.canDragResize=false;
        this._outline.left.canDragResize=false;
        this._outline.bottom.canDragResize=false;
        this._outline.right.canDragResize=false;
        if(resizeFrom){
            if(!isc.isAn.Array(resizeFrom))resizeFrom=[resizeFrom];
            for(var i=0;i<resizeFrom.length;i++){
                var edgeName=resizeFrom[i],
                    outline=null
                ;
                if(edgeName=="T"){
                    outline=this._outline.top;
                }else if(edgeName=="L"){
                    outline=this._outline.left;
                }else if(edgeName=="B"){
                    outline=this._outline.bottom;
                }else if(edgeName=="R"){
                    outline=this._outline.right;
                }else{
                    continue;
                }
                outline.dragTarget=object;
                outline.canDragResize=true;
            }
            this._resizeFrom=resizeFrom;
        }
        if(showLabel||(showLabel==null&&this.showLabel)){
            if(this._outline.label==null){
                this._createLabel();
            }else{
                this._outline.label.setBackgroundColor(this.labelBackgroundColor);
            }
            this._outline.label.setContents(label);
            this._showingLabel=true;
            this._labelText=label;
        }else{
            this._outline.label=null;
            this._showingLabel=false;
            this._labelText=null;
        }
        this._object=object;
        this._resetOutline();
        this.delayCall("_moveOutline",[],0);
        this.delayCall("showOutline",[],0);
        if(object.moved){
            this._observer.observe(object,"moved",
                       "isc.Timer.setTimeout('isc.SelectionOutline._moveOutline()',0)");
        }
        if(object.resized){
            this._observer.observe(object,"resized",
                       "isc.Timer.setTimeout('isc.SelectionOutline._resizeOutline()',0)");
        }
        var scrollObj=isc.isA.FormItem(object)?object.form:object;
        while(scrollObj){
            if(scrollObj.scrolled){
                this._observer.observe(scrollObj,"scrolled",
                        "isc.SelectionOutline._moveOutline()");
            }
            scrollObj=scrollObj.parentElement;
        }
        if(object.hide){
            this._observer.observe(object,"hide","isc.SelectionOutline.hideOutline()");
        }
        if(object.destroy){
            this._observer.observe(object,"destroy","isc.SelectionOutline.hideOutline()");
        }
        if(object._visibilityChanged){
            this._observer.observe(object,"_visibilityChanged","isc.SelectionOutline.visibilityChanged()");
        }
        if(flash!=false)this._flashOutline()
    }
,isc.A.deselect=function isc_c_SelectionOutline_deselect(){
        this.hideDragHandle();
        if(this._outline)this.hideOutline();
        if(this._observer&&this._object){
            this._observer.ignore(this._object,"moved");
            this._observer.ignore(this._object,"resized");
            this._observer.ignore(this._object,"hide");
            this._observer.ignore(this._object,"destroy");
            this._observer.ignore(this._object,"_visibilityChanged");
            var scrollObj=isc.isA.FormItem(this._object)?this._object.form:this._object;
            while(scrollObj){
                this._observer.ignore(scrollObj,"scrolled");
                scrollObj=scrollObj.parentElement;
            }
            this._outline.top.canDragResize=false;
            this._outline.left.canDragResize=false;
            this._outline.bottom.canDragResize=false;
            this._outline.right.canDragResize=false;
        }
        this._object=null;
    }
,isc.A.getSelectedObject=function isc_c_SelectionOutline_getSelectedObject(){
        return this._object;
    }
,isc.A._createOutline=function isc_c_SelectionOutline__createOutline(object,label,showLabel){
        var baseProperties={
            autoDraw:false,
            overflow:"hidden",
            border:this.border,
            padding:0
        }
        this._outline={
            top:isc.Canvas.create(isc.addProperties(baseProperties,{
                        snapTo:"T",
                        snapEdge:"B",
                        width:"100%",
                        height:2,
                        canDragResize:false,
                        resizeFrom:["T"]
                  })),
            left:isc.Canvas.create(isc.addProperties(baseProperties,{
                        snapTo:"L",
                        snapEdge:"R",
                        width:2,
                        height:"100%",
                        canDragResize:false,
                        resizeFrom:["L"]
                   })),
            bottom:isc.Canvas.create(isc.addProperties(baseProperties,{
                        snapTo:"B",
                        snapEdge:"T",
                        width:"100%",
                        height:2,
                        canDragResize:false,
                        resizeFrom:["B"]
                    })),
            right:isc.Canvas.create(isc.addProperties(baseProperties,{
                        snapTo:"R",
                        snapEdge:"L",
                        width:2,
                        height:"100%",
                        canDragResize:false,
                        resizeFrom:["R"]
                  }))
        }
        this._observer=isc.Class.create();
    }
,isc.A._createLabel=function isc_c_SelectionOutline__createLabel(){
        if(this._cachedLabel){
            this._outline.label=this._cachedLabel;
            return;
        }
        this._cachedLabel=this._outline.label=isc.Label.create({
            autoDraw:true,top:-100,left:-100,
            autoFit:true,
            autoFitDirection:"both",
            padding:2,
            wrap:false,
            isMouseTransparent:true,
            backgroundColor:this.labelBackgroundColor,
            opacity:this.labelOpacity,
            snapTo:this.labelSnapTo,
            snapEdge:this.labelSnapEdge,
            snapOffsetTop:this.labelSnapOffset,
            mouseOver:function(){
                if(this._movedAway){
                    isc.Timer.clear(this._snapBackTimer);
                    isc.SelectionOutline._moveOutline();
                    this._movedAway=false;
                }else{
                    var _this=this;
                    this._slideAwayTimer=isc.Timer.setTimeout(function(){
                        _this._slideAway();
                    },300);
                }
            },
            mouseOut:function(){
                if(this._slideAwayTimer){
                    isc.Timer.clear(this._slideAwayTimer);
                    delete this._slideAwayTimer;
                }
            },
            _slideAway:function(){
                isc.Timer.clear(this._snapBackTimer);
                this._movedAway=true;
                this.animateMove(null,(this.getPageTop()+this.getVisibleHeight())-
                                         isc.SelectionOutline.labelSnapOffset,null,200);
                this._snapBackTimer=isc.Timer.setTimeout(function(){
                    isc.SelectionOutline._moveOutline();
                    if(isc.SelectionOutline._outline.label){
                        isc.SelectionOutline._outline.label._movedAway=false;
                    }
                },3000);
            }
            });
    }
,isc.A._resizeOutline=function isc_c_SelectionOutline__resizeOutline(){
        this.logInfo("Resizing selected object "+this._object,"selectionOutline");
        this._refreshOutline();
    }
,isc.A._moveOutline=function isc_c_SelectionOutline__moveOutline(){
        this.logInfo("Moving selected object "+this._object,"selectionOutline");
        this._refreshOutline();
    }
,isc.A._refreshOutline=function isc_c_SelectionOutline__refreshOutline(){
        if(!this._object||this._object.destroyed||this._object.destroying)return;
        this._outline.top.resizeTo(this._object.getVisibleWidth(),this._outline.top.height);
        this._outline.bottom.resizeTo(this._object.getVisibleWidth(),this._outline.bottom.height);
        this._outline.left.resizeTo(this._outline.left.width,this._object.getVisibleHeight());
        this._outline.right.resizeTo(this._outline.right.width,this._object.getVisibleHeight());
        var isACanvas=isc.isA.Canvas(this._object);
        for(var key in this._outline){
            var piece=this._outline[key];
            if(piece==null)continue;
            if(isACanvas){
                isc.Canvas.snapToEdge(this._object,piece.snapTo,piece,piece.snapEdge,
                                      this._object);
            }else{
                isc.Canvas.snapToEdge(this._object.getPageRect(),piece.snapTo,piece,
                                      piece.snapEdge);
            }
        }
        this.positionDragHandle();
    }
,isc.A._flashOutline=function isc_c_SelectionOutline__flashOutline(){
        var borders=[this.border,this.flashBorder];
        for(var i=0;i<this.flashCount;i++){
            isc.Timer.setTimeout({
                    target:this,methodName:"_setOutline",
                    args:[borders[i%2]]
            },(this.flashInterval*i)
            )
        }
    }
,isc.A._resetOutline=function isc_c_SelectionOutline__resetOutline(){
        this._setOutline(this.border);
    }
,isc.A._setOutline=function isc_c_SelectionOutline__setOutline(border){
        for(var key in this._outline){
            if(key=="label")continue;
            var piece=this._outline[key];
            piece.setBorder(border);
        }
    }
,isc.A.hideOutline=function isc_c_SelectionOutline_hideOutline(){
        if(!this._outline)return;
        for(var key in this._outline){
            if(this._outline[key])this._outline[key].hide();
        }
        this._visible=false;
        this.hideDragHandle();
    }
,isc.A.showOutline=function isc_c_SelectionOutline_showOutline(){
        if(!this._outline||!this.getSelectedObject())return;
        for(var key in this._outline){
            if(this._outline[key])this._outline[key].show();
        }
        this._visible=true;
    }
,isc.A.showDragHandle=function isc_c_SelectionOutline_showDragHandle(){
        var dragTarget=isc.SelectionOutline.getSelectedObject();
        if(!dragTarget)return;
        if(!this._dragHandle){
            var _this=this;
            this._dragHandle=isc.Img.create({
                src:"[SKIN]/../../ToolSkin/images/controls/dragHandle.gif",
                prompt:"Grab here to drag component",
                width:this._dragHandleWidth,height:this._dragHandleHeight,
                cursor:"move",
                backgroundColor:"white",
                opacity:80,
                canDrag:true,
                canDrop:true,
                isMouseTransparent:true,
                mouseDown:function(){
                    this.dragIconOffsetX=isc.EH.getX()-
                                              isc.SelectionOutline.draggingObject.getPageLeft();
                    this.dragIconOffsetY=isc.EH.getY()-
                                              isc.SelectionOutline.draggingObject.getPageTop();
                    _this._mouseDown=true;
                    this.Super("mouseDown",arguments);
                },
                mouseUp:function(){
                    _this._mouseDown=false;
                }
            });
        }
        if(this.draggingObject&&this.draggingObject==dragTarget){
            this._dragHandle.show();
        }
        if(this.draggingObject){
            this.observer.ignore(this.draggingObject,"dragMove");
            this.observer.ignore(this.draggingObject,"dragStop");
            this.observer.ignore(this.draggingObject,"hide");
            this.observer.ignore(this.draggingObject,"destroy");
            this.observer.ignore(this.draggingObject,"_visibilityChanged");
            if(this._keyPressEventID){
                isc.Page.clearEvent("keyPress",this._keyPressEventID);
                delete this._keyPressEventID;
            }
        }
        if(isc.isA.FormItem(dragTarget)){
            if(!this._dragTargetProxy){
                this._dragTargetProxy=isc.FormItemProxyCanvas.create();
            }
            this._dragTargetProxy.delayCall("setFormItem",[dragTarget]);
            dragTarget=this._dragTargetProxy;
            for(var key in this._outline){
                if(key=="label")continue;
                var piece=this._outline[key];
                if(piece.canDragResize)piece.dragTarget=dragTarget;
            }
        }
        if(!dragTarget.editProxy){
            this.draggingObject=null;
            this._dragHandle.hide();
            return;
        }
        this._dragHandle.setProperties({dragTarget:dragTarget});
        isc.Timer.setTimeout("isc.SelectionOutline.positionDragHandle()",0);
        if(!this.observer)this.observer=isc.Class.create();
        this.draggingObject=dragTarget;
        this.observer.observe(this.draggingObject,"dragMove",
                    "isc.SelectionOutline.positionDragHandle(true)");
        this.observer.observe(this.draggingObject,"dragStop",
                    "isc.SelectionOutline._mouseDown = false");
        this.observer.observe(this.draggingObject,"hide",
                    "isc.SelectionOutline._dragHandle.hide();");
        this.observer.observe(this.draggingObject,"destroy",
                    "isc.SelectionOutline._dragHandle.hide()");
        this.observer.observe(this.draggingObject,"_visibilityChanged",
                    "isc.SelectionOutline.visibilityChanged()");
        if(!this._keyPressEventID){
            this._keyPressEventID=isc.Page.setEvent("keyPress",this);
        }
        this._dragHandle.show();
    }
,isc.A.positionDragHandle=function isc_c_SelectionOutline_positionDragHandle(offset){
        if(!this._dragHandle||!this.draggingObject)return;
        var selected=this.draggingObject;
        if(selected.destroyed||selected.destroying){
            this.logWarn("target of dragHandle: "+isc.Log.echo(selected)+" is invalid: "+
                         selected.destroyed?"already destroyed"
                                            :"currently in destroy()");
            return;
        }
        var height=selected.getVisibleHeight();
        if(height<this._dragHandleHeight*2){
            this._dragHandleYOffset=Math.round((height-this._dragHandle.height)/2)-1;
        }else{
            this._dragHandleYOffset=-1;
        }
        if(selected.isA("FormItemProxyCanvas")&&!this._mouseDown){
            selected.syncWithFormItemPosition();
        }
        if(!selected)return;
        var left=selected.getPageLeft()+this._dragHandleXOffset;
        if(offset){
            left+=selected.getOffsetX()-this._dragHandle.dragIconOffsetX;
        }
        this._dragHandle.setPageLeft(left);
        var top=selected.getPageTop()+this._dragHandleYOffset;
        if(offset){
            top+=selected.getOffsetY()-this._dragHandle.dragIconOffsetY;
        }
        this._dragHandle.setPageTop(top);
        this._dragHandle.bringToFront();
    }
,isc.A.hideDragHandle=function isc_c_SelectionOutline_hideDragHandle(){
        if(this._dragHandle){
            this._dragHandle.hide();
            if(this._keyPressEventID){
                isc.Page.clearEvent("keyPress",this._keyPressEventID);
                delete this._keyPressEventID;
            }
        }
    }
,isc.A.hideProxyCanvas=function isc_c_SelectionOutline_hideProxyCanvas(){
        if(this._dragTargetProxy)this._dragTargetProxy.hide();
    }
,isc.A.visibilityChanged=function isc_c_SelectionOutline_visibilityChanged(){
        var object=isc.SelectionOutline.getSelectedObject();
        if(!object)return;
        if(object.isVisible())isc.SelectionOutline.showOutline();
        else isc.SelectionOutline.hideOutline();
        if(this._dragHandle&&this.draggingObject&&this.draggingObject.isVisible()){
            this._dragHandle.show();
        }
    }
,isc.A.pageKeyPress=function isc_c_SelectionOutline_pageKeyPress(target,eventInfo){
        var object=isc.SelectionOutline.getSelectedObject();
        if(!object||!object.parentElement)return;
        var key=isc.EH.getKeyEventCharacter();
        if(!isc.isA.AlphaNumericChar(key)){
            var parent=object.parentElement,
                shiftPressed=isc.EH.shiftKeyDown(),
                vGap=(shiftPressed?1:parent.snapVGap),
                hGap=(shiftPressed?1:parent.snapHGap),
                delta=[0,0]
            ;
            switch(isc.EH.getKey()){
            case"Arrow_Up":
                delta=[0,vGap*-1];
                break;
            case"Arrow_Down":
                delta=[0,vGap];
                break;
            case"Arrow_Left":
                delta=[hGap*-1,0];
                break;
            case"Arrow_Right":
                delta=[hGap,0];
                break;
            }
            if(delta[0]!=0||delta[1]!=0){
                if(object.snapTo){
                    object.setSnapOffsetLeft((object.snapOffsetLeft||0)+delta[0]);
                    object.setSnapOffsetTop((object.snapOffsetTop||0)+delta[1]);
                }else{
                    object.moveBy(delta[0],delta[1]);
                }
            }
        }
    }
);
isc.B._maxIndex=isc.C+23;

isc.ClassFactory.defineClass("Repo","Class");
isc.A=isc.Repo.getPrototype();
isc.A.idField="id";
isc.A.viewNameField="viewName";
isc.A.objectField="object"
    objectFormat:"js"
;

isc.A=isc.Repo.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.B.push(isc.A.init=function isc_Repo_init(){
    this.initDataSource();
}
,isc.A.initDataSource=function isc_Repo_initDataSource(){
    if(this.dataSource&&!isc.isA.DataSource(this.dataSource))
        this.dataSource=isc.DS.getDataSource(this.dataSource);
}
,isc.A.destroy=function isc_Repo_destroy(){
    this.Super("destroy",arguments);
}
,isc.A.loadObjects=function isc_Repo_loadObjects(context,callback){
}
,isc.A.loadObject=function isc_Repo_loadObject(context,callback){
}
,isc.A.saveObject=function isc_Repo_saveObject(contents,context,callback){
}
,isc.A.showLoadUI=function isc_Repo_showLoadUI(context,callback){
}
,isc.A.showSaveUI=function isc_Repo_showSaveUI(contents,context,callback){
}
,isc.A.isActive=function isc_Repo_isActive(){
    if(this._loadFileDialog&&this._loadFileDialog.isVisible())return true;
    if(this._saveFileDialog&&this._saveFileDialog.isVisible())return true;
    return false;
}
,isc.A.customFormatToJS=function isc_Repo_customFormatToJS(value){
    return value;
}
);
isc.B._maxIndex=isc.C+10;

isc.Repo.addClassProperties({
})
isc.Repo.registerStringMethods({
});
isc.ClassFactory.defineClass("ViewRepo","Repo");
isc.A=isc.ViewRepo.getPrototype();
isc.A.dataSource="Filesystem";
isc.A.idField="name";
isc.A.viewNameField="name";
isc.A.objectField="contents";
isc.A.objectFormat="xml"
;

isc.A=isc.ViewRepo.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.B.push(isc.A.loadObjects=function isc_ViewRepo_loadObjects(context,callback){
    this.initDataSource();
    var ds=this.dataSource,
        _this=this;
    ds.fetchData(context?context.criteria:null,
        function(dsResponse){
            _this.loadObjectsReply(dsResponse.data,context,callback);
        }
    );
}
,isc.A.loadObjectsReply=function isc_ViewRepo_loadObjectsReply(data,context,callback){
    this.fireCallback(callback,"objects, context",[data,callback]);
}
,isc.A.loadObject=function isc_ViewRepo_loadObject(context,callback){
    this.initDataSource();
    var ds=this.dataSource,
        _this=this;
    ds.fetchData(context?context.criteria:null,
        function(dsResponse){
            _this.loadObjectReply(dsResponse.data,context,callback);
        },{operationId:"loadFile"}
    );
}
,isc.A.loadObjectReply=function isc_ViewRepo_loadObjectReply(data,context,callback){
    var record=isc.isAn.Array(data)?data[0]:data,
        value=record[this.objectField]
    ;
    if(this.objectFormat=="custom"){
        value=this.customFormatToJS(value);
    }
    context[this.idField]=context.fileName=record[this.idField];
    context[this.viewNameField]=context.screenName=record[this.viewNameField];
    if(context.screenName.indexOf(".")>0)
        context.screenName=context.screenName.substring(0,context.screenName.indexOf("."));
    context[this.objectField]=value;
    context.record=record;
    this.fireCallback(callback,"contents,context",[value,context]);
}
,isc.A.createLoadDialog=function isc_ViewRepo_createLoadDialog(context){
    var dialog=isc.TLoadFileDialog.create({
        directoryListingProperties:{
            canEdit:false
        },
        title:"Load View",
        initialDir:context.caller.workspacePath,
        rootDir:context.caller.workspacePath,
        fileFilter:".xml$",
        actionStripControls:["spacer:10","pathLabel","previousFolderButton","spacer:10",
                 "upOneLevelButton","spacer:10",
                 "refreshButton","spacer:2"
        ]
    });
    dialog.show();
    dialog.hide();
    return dialog;
}
,isc.A.showLoadUI=function isc_ViewRepo_showLoadUI(context,callback){
    var _this=this;
    if(!this._loadFileDialog){
        this._loadFileDialog=isc.TLoadFileDialog.create({
            directoryListingProperties:{
                canEdit:false
            },
            title:"Load View",
            initialDir:context.caller.workspacePath,
            rootDir:context.caller.workspacePath,
            fileFilter:".xml$",
            actionStripControls:["spacer:10","pathLabel","previousFolderButton","spacer:10",
                     "upOneLevelButton","spacer:10",
                     "refreshButton","spacer:2"
            ],
            loadFile:function(fileName){
                var name=fileName;
                if(name.endsWith(".jsp")||name.endsWith(".xml")){
                    name=name.substring(0,name.lastIndexOf("."));
                }
                _this.loadObject(
                    isc.addProperties(
                        {},
                        this._loadContext,
                        {criteria:{path:this.currentDir+"/"+fileName}}
                        ),
                    this._loadCallback
                );
                this.hide();
            }
        });
    }else{
        this._loadFileDialog.directoryListing.data.invalidateCache();
    }
    this._loadFileDialog._loadContext=context;
    this._loadFileDialog._loadCallback=callback;
    this._loadFileDialog.show();
}
,isc.A.saveObject=function isc_ViewRepo_saveObject(contents,context,callback){
    var fileName=context.fileName,
        dotIndex=fileName.lastIndexOf("."),
        code=contents,
        _builder=context.caller
    ;
    this.initDataSource();
    code=code.replaceAll("dataSource=\"ref:","dataSource=\"");
    if(dotIndex!=null&&(fileName.endsWith(".jsp")||fileName.endsWith(".xml"))){
        fileName=fileName.substring(0,dotIndex);
    }
    var index=fileName.lastIndexOf("/");
    var screenName=index>=0?fileName.substring(index+1):fileName,
        fileNameWithoutExtension=_builder.workspacePath+"/"+screenName,
        xmlFileName=fileNameWithoutExtension+".xml",
        ds=this.dataSource
    ;
    context.screenName=screenName;
    ds.updateData({path:xmlFileName,contents:code},
        null,{operationId:"saveFile",showPrompt:!context.suppressPrompt}
    );
    var page='<%@ page contentType="text/html; charset=UTF-8"%>\n'+
        '<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic" %>\n'+
        '<HTML><HEAD><TITLE>'+
        screenName+
        '</TITLE>\n'+
        '<isomorphic:loadISC skin="'+
        _builder.skin+
        '"'+
        (_builder.modulesDir?'modulesDir="'+_builder.modulesDir+'"':"")+
        (context.additionalModules?(' includeModules="'+context.additionalModules+'"'):"")
        +'/>\n </HEAD><BODY>\n';
    for(var i=0;i<_builder.globalDependencies.deps.length;i++){
        var dep=_builder.globalDependencies.deps[i];
        if(dep.type=="js"){
            page+='<SCRIPT SRC='+
            (dep.url.startsWith("/")?
                _builder.webRootRelWorkspace:
                _builder.basePathRelWorkspace+"/"
                )+
            dep.url+
            '></SCRIPT>\n';
        }
        else
            if(dep.type=="schema"){
                page+='<SCRIPT>\n<isomorphic:loadDS name="'+dep.id+'"/></SCRIPT>\n';
            }
            else
                if(dep.type=="ui"){
                    page+='<SCRIPT>\n<isomorphic:loadUI name="'+dep.id+'"/></SCRIPT>\n';
                }
                else
                    if(dep.type=="css"){
                        page+='<LINK REL="stylesheet" TYPE="text/css" HREF='+
                        (dep.url.startsWith("/")?
                            _builder.webRootRelWorkspace:
                            _builder.basePathRelWorkspace+"/"
                            )+
                        dep.url+
                        '>\n';
                    }
    }
    page+='<SCRIPT>\n'+
        'isc.Page.setAppImgDir("'+_builder.basePathRelWorkspace+'/graphics/");\n'+
        '<isomorphic:XML>\n'+code+'\n</isomorphic:XML>'+
        '</SCRIPT>\n'+
        '</BODY></HTML>';
    _builder.projectComponents._tempScreen=screenName;
    var jspFileName=fileNameWithoutExtension+".jsp";
    ds.updateData({path:jspFileName,contents:page},
        function(){
            if(callback){
                isc.Class.fireCallback(callback,"success,context",[true,context]);
            }
            if(context.suppressPrompt)return;
            var url=window.location.href;
            if(url.indexOf("?")>0)url=url.substring(0,url.indexOf("?"));
            url=url.substring(0,url.lastIndexOf("/"));
            url+=(url.endsWith("/")?"":"/")+_builder.workspaceURL+screenName+".jsp";
            isc.say("Your screen can be accessed at:<P>"+
                "<a target=_blank href='"+
                url+
                "'>"+
                url+
                "</a>");
        },
        {operationId:"saveFile",showPrompt:!context.suppressPrompt}
    );
    if(_builder.saveURL){
        isc.RPCManager.send(null,null,
            {
                actionURL:_builder.saveURL,
                useSimpleHttp:true,
                showPrompt:!context.suppressPrompt,
                params:{
                    screen:code
                }
            }
        );
    }
}
,isc.A.showSaveUI=function isc_ViewRepo_showSaveUI(contents,context,callback){
    var _builder=context.caller,
        _this=this,
        code=contents,
        explicitScreenName=(context.saveAs?"":context.screenName),
        _callback=callback
    ;
    if(!this._saveFileDialog){
        this._saveFileDialog=isc.TSaveFileDialog.create({
            title:"Save View",
            fileFilter:".xml$",
            visibility:"hidden",
            actionStripControls:["spacer:10","pathLabel","previousFolderButton","spacer:10","upOneLevelButton","spacer:10","refreshButton","spacer:2"],
            directoryListingProperties:{
                canEdit:false
            },
            initialDir:_builder.workspacePath,
            rootDir:_builder.workspacePath,
            saveFile:function(fileName){
                _this.saveObject(
                    this._saveCode,
                    isc.addProperties(
                        this._saveContext,
                        {fileName:fileName}
                        ),
                    this._saveCallback
                    );
                this.hide();
            }
        })
    }
    else{
        this._saveFileDialog.directoryListing.data.invalidateCache();
    }
    this._saveFileDialog._saveCode=code;
    this._saveFileDialog._saveContext=context;
    this._saveFileDialog._saveCallback=callback;
    if(explicitScreenName&&explicitScreenName!=""){
        return this._saveFileDialog.saveFile(explicitScreenName);
    }
    this._saveFileDialog.show();
}
);
isc.B._maxIndex=isc.C+8;

isc.ClassFactory.defineClass("DSViewRepo","Repo");
isc.A=isc.DSViewRepo.getPrototype();
isc.A.idField="id";
isc.A.viewNameField="viewName";
isc.A.objectField="object"
;

isc.A=isc.DSViewRepo.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.B.push(isc.A.loadObjects=function isc_DSViewRepo_loadObjects(context,callback){
    if(!this.dataSource){
        this.logWarn("No dataSource available in "+this.getClassName()+".loadObjects");
        return;
    }
    this.initDataSource();
    var ds=this.dataSource,
        _this=this;
    ds.fetchData(context.criteria,
        function(dsResponse){
            _this.loadObjectsReply(dsResponse.data,context,callback);
        }
    );
}
,isc.A.loadObjectsReply=function isc_DSViewRepo_loadObjectsReply(data,context,callback){
    this.fireCallback(callback,"data, context",[data,context]);
}
,isc.A.loadObject=function isc_DSViewRepo_loadObject(context,callback){
    if(!this.dataSource){
        this.logWarn("No dataSource available in "+this.getClassName()+".loadObject");
        return;
    }
    this.initDataSource();
    var _this=this,
        ds=this.dataSource;
    ds.fetchData(context.criteria,
        function(dsRequest){
            _this.loadObjectReply(dsRequest.data,context,callback);
        }
    );
}
,isc.A.loadObjectReply=function isc_DSViewRepo_loadObjectReply(data,context,callback){
    var record=isc.isAn.Array(data)?data[0]:data,
        value=record[this.objectField]
    ;
    if(this.objectFormat=="custom"){
        value=this.customFormatToJS(value);
    }
    context[this.idField]=record[this.idField];
    context[this.viewNameField]=context.screenName=record[this.viewNameField];
    context[this.objectField]=value;
    context.record=record;
    this.fireCallback(callback,"contents,context",[value,context]);
}
,isc.A.saveObject=function isc_DSViewRepo_saveObject(contents,context,callback){
    if(!this.dataSource){
        this.logWarn("No dataSource available in "+this.getClassName()+".saveObject");
        return;
    }
    this.initDataSource();
    var _this=this,
        ds=this.dataSource;
    contents=contents.replaceAll("dataSource=\"ref:","dataSource=\"");
    var record={};
    if(context[this.idField])record[this.idField]=context[this.idField];
    record[this.viewNameField]=context[this.viewNameField];
    record[this.objectField]=contents;
    if(!record[this.idField]){
        ds.addData(record,
            function(dsResponse){
                _this.saveObjectReply(dsResponse,callback,context);
            }
        );
    }else{
        ds.updateData(record,
            function(dsResponse){
                _this.saveObjectReply(dsResponse,callback,context);
            }
        );
    }
}
,isc.A.saveObjectReply=function isc_DSViewRepo_saveObjectReply(dsResponse,callback,context){
    if(callback)this.fireCallback(callback,"success",[true]);
}
,isc.A.showLoadUI=function isc_DSViewRepo_showLoadUI(context,callback){
    var _this=this;
    if(!this._loadFileDialog){
        this._loadFileDialog=isc.TLoadFileDialog.create({
            showPreviousFolderButton:false,
            showUpOneLevelButton:false,
            showCreateNewFolderButton:false,
            actionFormProperties:{
                process:function(){
                    if(this.validate())
                        this.creator.recordSelected(this.creator.directoryListing._lastRecord);
                }
            },
            directoryListingProperties:{
                canEdit:false,
                dataSource:this.dataSource,
                fields:[
                    {name:_this.idField,width:0},
                    {name:_this.viewNameField,width:"*"}
                ],
                recordDoubleClick:function(viewer,record){
                    if(record.isFolder){
                        this.creator.setDir(record.path);
                    }else{
                        this.creator.recordSelected(record);
                    }
                    return false;
                }
            },
            dataSource:this.dataSource,
            title:"Load View",
            fileFilter:".xml$",
            actionStripControls:["spacer:10","pathLabel","previousFolderButton","spacer:10",
                     "upOneLevelButton","spacer:10",
                     "refreshButton","spacer:2"
            ],
            recordSelected:function(record){
                this._loadContext.criteria={record:record};
                this._loadContext.criteria[_this.idField]=record[_this.idField];
                _this.loadObject(this._loadContext,this._loadCallback);
                this.hide();
            }
        })
    }else{
        this._loadFileDialog.directoryListing.data.invalidateCache();
    }
    this._loadFileDialog._loadContext=context;
    this._loadFileDialog._loadCallback=callback;
    this._loadFileDialog.show();
}
,isc.A.showSaveUI=function isc_DSViewRepo_showSaveUI(contents,context,callback){
    var _this=this;
    if(context.screenName){
        this.saveObject(contents,context,callback);
        return;
    }
    if(!this._saveFileDialog){
        this._saveFileDialog=isc.TSaveFileDialog.create({
            title:"Save File",
            actionButtonTitle:"Save",
            showPreviousFolderButton:false,
            showUpOneLevelButton:false,
            showCreateNewFolderButton:false,
            actionFormProperties:{
                process:function(){
                    if(this.validate())
                        this.creator.recordSelected(this.creator.directoryListing._lastRecord);
                }
            },
            directoryListingProperties:{
                canEdit:false,
                dataSource:this.dataSource,
                fields:[
                    {name:_this.idField,width:0},
                    {name:_this.viewNameField,width:"*"}
                ],
                recordDoubleClick:function(viewer,record){
                    if(record.isFolder){
                        this.creator.setDir(record.path);
                    }else{
                        this.creator.recordSelected(record);
                    }
                    return false;
                }
            },
            dataSource:this.dataSource,
            title:"Load View",
            fileFilter:".xml$",
            actionStripControls:["spacer:10","pathLabel","previousFolderButton","spacer:10",
                     "upOneLevelButton","spacer:10",
                     "refreshButton","spacer:2"
            ],
            recordSelected:function(record){
                var context=this._saveContext;
                if(record){
                    context.criteria[_this.idField]=record[_this.idField];
                    context.record=record;
                    context[_this.idField]=record[_this.idField];
                    context[_this.viewNameField]=record[_this.viewNameField];
                }else{
                    context[_this.viewNameField]=this.actionForm.getValue("fileName");
                    context[_this.idField]=null;
                }
                _this.saveObject(this._saveContents,context,this._saveCallback);
                this.hide();
            }
        })
    }else{
        this._saveFileDialog.directoryListing.data.invalidateCache();
    }
    this._saveFileDialog._saveContents=contents;
    this._saveFileDialog._saveContext=context;
    this._saveFileDialog._saveCallback=callback;
    this._saveFileDialog.show();
}
);
isc.B._maxIndex=isc.C+8;

isc.ClassFactory.defineClass("DSRepo","Repo");
isc.DSRepo.addProperties({
})
isc.A=isc.DSRepo.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.B.push(isc.A.loadObjects=function isc_DSRepo_loadObjects(context,callback){
    var _this=this;
    if(!this.dataSource){
        isc.DMI.call({
            appID:"isc_builtin",
            className:"com.isomorphic.tools.BuiltinRPC",
            methodName:"getDefinedDataSources",
            args:[],
            callback:function(response){
                _this.loadObjectsReply(response.data,context,callback);
            }
        });
    }else{
        this.initDataSource();
        this.dataSource.fetchData(context?context.criteria:null,
            function(dsResponse){
                _this.loadObjectsReply(dsResponse.data,context,callback);
            }
        );
    }
}
,isc.A.loadObjectsReply=function isc_DSRepo_loadObjectsReply(data,context,callback){
    this.fireCallback(callback,"objects, context",[data,context]);
}
,isc.A.showLoadUI=function isc_DSRepo_showLoadUI(context,callback){
    if(!this._pickDataSourceDialog){
        this._pickDataSourceDialog=isc.PickDataSourceDialog.create();
    }
    var self=this;
    this.loadObjects(null,function(data){
        self._pickDataSourceDialog.callback=function(records){
            if(!isc.isAn.Array(records))records=[records];
            self.fireCallback(callback,"records, context",[records,context]);
        }
        self._pickDataSourceDialog.setData(data);
        self._pickDataSourceDialog.show();
    });
}
);
isc.B._maxIndex=isc.C+3;

isc.ClassFactory.defineClass("PickDataSourceDialog","Window");
isc.A=isc.PickDataSourceDialog.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.title="DataSource Picker";
isc.A.autoCenter=true;
isc.A.modal=true;
isc.A.width=460;
isc.A.height=300;
isc.A.canDragResize=true;
isc.A.bodyConstructor="VLayout";
isc.A.dsListingConstructor="ListGrid";
isc.A.dsListingDefaults={
        fields:[
            {name:"dsName",title:"Name",width:"*"},
            {name:"dsType",title:"Type",width:150}
        ],
        emptyMessage:"Retrieving list of DataSources ...",
        height:"*",
        selectionType:"multiple",
        canMultiSort:true,
        initialSort:[
            {property:"dsName",direction:"ascending"}
        ],
        recordDoubleClick:function(viewer,record){
            this.creator.dataSourceSelected(record);
            return false;
        },
        selectionUpdated:function(record){
            this.creator.pickButton.setDisabled(!record);
        }
    };
isc.A.pickButtonConstructor="Button";
isc.A.pickButtonDefaults={
        title:"Select DataSource",
        width:150,
        layoutAlign:"right",
        height:30,
        margin:5,
        action:function(){
            this.creator.dataSourceSelected(this.creator.dsListing.getSelectedRecords());
        }
    };
isc.B.push(isc.A.setData=function isc_PickDataSourceDialog_setData(data){
        this.dsListing.emptyMessage="No DataSources found.";
        this.dsListing.setData(data);
        this.pickButton.setDisabled(true);
    }
,isc.A.dataSourceSelected=function isc_PickDataSourceDialog_dataSourceSelected(record){
        this.hide();
        this.fireCallback(this.callback,"record",[record]);
    }
,isc.A.initWidget=function isc_PickDataSourceDialog_initWidget(){
        this.Super("initWidget",arguments);
        this.dsListing=this.createAutoChild("dsListing");
        this.pickButton=this.createAutoChild("pickButton");
        this.addItems([
            this.dsListing,
            this.pickButton
        ]);
    }
);
isc.B._maxIndex=isc.C+3;

if(!isc.TScrollbar)isc.defineClass("TScrollbar","Scrollbar");
if(!isc.TPropertySheet)isc.defineClass("TPropertySheet","PropertySheet");
if(!isc.TSectionItem)isc.defineClass("TSectionItem","SectionItem");
if(!isc.TSectionStack)isc.defineClass("TSectionStack","SectionStack");
if(!isc.TSectionHeader)isc.defineClass("TSectionHeader","SectionHeader");
if(!isc.TImgSectionHeader)isc.defineClass("TImgSectionHeader","ImgSectionHeader");
if(!isc.TButton)isc.defineClass("TButton","StretchImgButton");
if(!isc.TAutoFitButton)isc.defineClass("TAutoFitButton","TButton");
if(!isc.TMenuButton)isc.defineClass("TMenuButton","MenuButton");
if(!isc.TMenu)isc.defineClass("TMenu","Menu");
if(!isc.TTabSet)isc.defineClass("TTabSet","TabSet")
if(!isc.TTreePalette)isc.defineClass("TTreePalette","TreePalette");
if(!isc.TEditTree)isc.defineClass("TEditTree","EditTree");
if(!isc.THTMLFlow)isc.defineClass("THTMLFlow","HTMLFlow");
if(!isc.TComponentEditor)isc.defineClass('TComponentEditor','ComponentEditor');
if(!isc.TDynamicForm)isc.defineClass('TDynamicForm','DynamicForm');
if(!isc.TLayout)isc.defineClass('TLayout','Layout');
if(!isc.TListPalette)isc.defineClass('TListPalette','ListPalette');
if(!isc.TSaveFileDialog)isc.defineClass("TSaveFileDialog","SaveFileDialog");
isc._debugModules = (isc._debugModules != null ? isc._debugModules : []);isc._debugModules.push('Tools');isc.checkForDebugAndNonDebugModules();isc._moduleEnd=isc._Tools_end=(isc.timestamp?isc.timestamp():new Date().getTime());if(isc.Log&&isc.Log.logIsInfoEnabled('loadTime'))isc.Log.logInfo('Tools module init time: ' + (isc._moduleEnd-isc._moduleStart) + 'ms','loadTime');delete isc.definingFramework;if (isc.Page) isc.Page.handleEvent(null, "moduleLoaded", { moduleName: 'Tools', loadTime: (isc._moduleEnd-isc._moduleStart)});}else{if(window.isc && isc.Log && isc.Log.logWarn)isc.Log.logWarn("Duplicate load of module 'Tools'.");}

/*

  SmartClient Ajax RIA system
  Version v10.0p_2014-09-18/EVAL Development Only (2014-09-18)

  Copyright 2000 and beyond Isomorphic Software, Inc. All rights reserved.
  "SmartClient" is a trademark of Isomorphic Software, Inc.

  LICENSE NOTICE
     INSTALLATION OR USE OF THIS SOFTWARE INDICATES YOUR ACCEPTANCE OF
     ISOMORPHIC SOFTWARE LICENSE TERMS. If you have received this file
     without an accompanying Isomorphic Software license file, please
     contact licensing@isomorphic.com for details. Unauthorized copying and
     use of this software is a violation of international copyright law.

  DEVELOPMENT ONLY - DO NOT DEPLOY
     This software is provided for evaluation, training, and development
     purposes only. It may include supplementary components that are not
     licensed for deployment. The separate DEPLOY package for this release
     contains SmartClient components that are licensed for deployment.

  PROPRIETARY & PROTECTED MATERIAL
     This software contains proprietary materials that are protected by
     contract and intellectual property law. You are expressly prohibited
     from attempting to reverse engineer this software or modify this
     software for human readability.

  CONTACT ISOMORPHIC
     For more information regarding license rights and restrictions, or to
     report possible license violations, please contact Isomorphic Software
     by email (licensing@isomorphic.com) or web (www.isomorphic.com).

*/

