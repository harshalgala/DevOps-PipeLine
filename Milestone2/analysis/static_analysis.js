var esprima = require("esprima");
var options = {tokens:true, tolerant: true, loc: true, range: true };
var fs = require("fs");

function main()
{

	console.log("-----------------------------------------------------------")
	console.log("Static Analysis Report")
	console.log("-----------------------------------------------------------")
	var args = process.argv.slice(2);

	const testFolder = '/var/lib/jenkins/workspace/checkboxio/server-side/site';
	//const fs = require('fs');

	// Stack overflow reference
	// Get all the files contained within dirctory and subdirectory
	function getFiles (dir, files_){
		files_ = files_ || [];
		var files = fs.readdirSync(dir);
		for (var i in files){
			var name = dir + '/' + files[i];
                        //console.log(name);
                        if(!dir.endsWith("node_modules")){
                        //console.log(name);
			if (fs.statSync(name).isDirectory()){
				getFiles(name, files_);
			} else {
				if(name.endsWith(".js")){
					files_.push(name);
				}
				
			}
                }
                }
		
		return files_;
	}
	
	var myArray = getFiles(testFolder);
	var arrayLength = myArray.length;
	for (var i = 0; i < arrayLength; i++) {
		complexity(myArray[i]);
	}

	if(Object.keys(builders).length==0){
		console.log("Build Successful. No code violations found");
	}
	
	// Report
	

	for( var node in builders )
	{
		
			
			var builder = builders[node];
			builder.report();
		
		
	}

    console.log("-----------------------------------------------------------")
	console.log("Static Analysis Completed")
	console.log("-----------------------------------------------------------")

}



var builders = {};

// Represent a reusable "class" following the Builder pattern.
function FunctionBuilder()
{
	this.StartLine = 0;
	this.FunctionName = "";
	
	// To be implemented
	this.LongMethod =0;
	this.SyncCalls=0;
	this.MessageChains=0;
	this.BigO=0;

	

	this.report = function()
	{
		console.log("Function name: " + this.FunctionName);
		if(this.LongMethod > 120){
			console.log("Violation Long Method : "  + this.LongMethod) ;	
		}
		if(this.BigO > 3){
			console.log("Violation Big O : "  + this.BigO) ;	
		}
		if(this.MessageChains > 3){
			console.log("Violation MessageChainsLength : "  + this.MessageChains) ;	
		}
		if(this.SyncCalls > 3){
			console.log("Violation SyncCalls : "  + this.SyncCalls) ;	
		}
		console.log("\n");
	}
};

// A builder for storing file level information.
function FileBuilder()
{
	this.FileName = "";
	// Number of strings in a file.
	this.Strings = 0;
	// Number of imports in a file.
	this.ImportCount = 0;

	this.report = function()
	{
		console.log (
			( "There are violations in file: {0}" 
			).format( this.FileName));
	}
}


// A function following the Visitor pattern.
// Annotates nodes with parent objects.
function traverseWithParents(object, visitor)
{
    var key, child;

    visitor.call(null, object);

    for (key in object) {
        if (object.hasOwnProperty(key)) {
            child = object[key];
            if (typeof child === 'object' && child !== null && key != 'parent') 
            {
            	child.parent = object;
					traverseWithParents(child, visitor);
            }
        }
    }
}


function traverseWithParents(object, visitor,level)
{
    var key, child;

    visitor.call(null, object);

    for (key in object) {
        if (object.hasOwnProperty(key)) {
            child = object[key];
            if (typeof child === 'object' && child !== null && key != 'parent') 
            {
				child.parent = object;
				child.level=level;
		
		
		
				traverseWithParents(child, visitor,level+1);
            }
        }
    }
}



function complexity(filePath)
{
	var buf = fs.readFileSync(filePath, "utf8");
	var ast = esprima.parse(buf, options);

	var i = 0;
     
	// A file level-builder:
	var fileBuilder = new FileBuilder();
	fileBuilder.FileName = filePath;
	fileBuilder.ImportCount = 0;

	var max1=0;

	maxcoun=[];

	//builders[filePath] = fileBuilder;

	// Tranverse program with a function visitor.
	traverseWithParents(ast, function (node) 
	{

		var max1=0;
		
			maxcoun=[];


		
		if (node.type === 'FunctionDeclaration' || node.type === 'FunctionExpression' ) 
		{
			var builder = new FunctionBuilder();

			builder.FunctionName = functionName(node);
			builder.StartLine = node.loc.start.line;
			// Extend this
			builder.EndLine =  node.loc.end.line;
			builder.LongMethod = builder.EndLine - builder.StartLine +1 ;

			
                     
            var synccount=0;
			var loopcount = [];
			var line=[];
			traverseWithParents(node, function(child){

				if(child.type == 'MemberExpression' || child.type =='CallExpression'){
					
					   	max1=message_chains(child);
					   maxcoun.push(max1);
					   line.push(child.line);
					  
				   }	
                      
if(child.type== 'CallExpression' && child.callee.type=='MemberExpression' && child.callee.property.name.endsWith("Sync") ){
                                //console.log(child.callee.property.name);
                                synccount++;
				}

				if(child.type== 'ForStatement' || child.type=='WhileStatement' || child.type=='ForInStatement' || child.type=='DoWhileStatement'  ){
					loopcount.push(child.level);
					//console.log(builder.FunctionName + "  " +child.level);
				};
			},1);

			builder.BigO = calculateBigO(loopcount);

	
			for(var i=0;i<maxcoun.length;i++){
				if(maxcoun[i]>max1){
					max1=maxcoun[i];
				}
	
			}
			builder.MessageChains= max1;
						builder.SyncCalls=synccount;
						
						if(buildError(builder)){
							builders[filePath] = fileBuilder;
							builders[builder.FunctionName]=builder;
						}			

		}

	
        

		

	});

}

function buildError(builder){
	 if(builder.LongMethod > 120 || builder.BigO > 3 || builder.MessageChains> 3 || builder.SyncCalls>1){
		//console.log(builder); 
		return true;
	} 
	return false;
}

function message_chains(node){
	if(node.type.indexOf("Expression") != -1){

		if("object" in node){
			//console.log("Node SEE"+node)
			//console.log("Object Name"+node.object.name);
		if(node.object.type === "Identifier" && node.property.type === "Identifier"){
			
			
		return 2;
		}
		if(node.object.type === "Identifier"){
			
		return 1;
		}
		else{
			  if(node.type.indexOf("Expression") != -1 && node.computed == true){
				return message_chains(node.object)
			} 	 
		return message_chains(node.object)+1;
		}
		
		}	


	if("callee" in node){
	if(node.callee.type === "Identifier"){
	return 1;
	}
	else{
	
	return message_chains(node.callee);
	}
	
	
}
	
	
	
	return 0;
}

	}




function calculateBigO(levels){
	if(levels.length<=1) return levels.length;
	var tmp=0;
	var length=0;

	for(var i=0;i<levels.length-1;i++){

			if(levels[i]<levels[i+1]){
				tmp++;
				if(length<tmp){
					length=tmp;
				}
			}
			else{
				tmp=0;
			}
		
	}	
	
	if(length===0) length=1;

	return length;
	

} 


// Helper function for counting children of node.
function childrenLength(node)
{
	var key, child;
	var count = 0;
	for (key in node) 
	{
		if (node.hasOwnProperty(key)) 
		{
			child = node[key];
			if (typeof child === 'object' && child !== null && key != 'parent') 
			{
				count++;
			}
		}
	}	
	return count;
}


// Helper function for checking if a node is a "decision type node"
function isDecision(node)
{
	if( node.type == 'IfStatement' || node.type == 'ForStatement' || node.type == 'WhileStatement' ||
		 node.type == 'ForInStatement' || node.type == 'DoWhileStatement')
	{
		return true	;
	}
	return false;
}

function isLoopStatement(node){
}

// Helper function for printing out function name.
function functionName( node )
{
	if( node.id )
	{
		return node.id.name;
	}
	return "anon function @" + node.loc.start.line;
}

// Helper function for allowing parameterized formatting of strings.
if (!String.prototype.format) {
  String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) { 
      return typeof args[number] != 'undefined'
        ? args[number]
        : match
      ;
    });
  };
}

main();

 exports.main = main;

