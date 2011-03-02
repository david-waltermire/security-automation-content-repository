package org.scapdev.content.repository.explorer
{
	import flash.net.FileReference;
	
	import mx.controls.Alert;
	
	/**
	 * Extends a FileReference so that we can override toString
	 */
	public class FileUploadStatus
	{
		private var ref:FileReference;
		private var response:XML;
		private var entitiesProcessed:int = -1;
		private var relationshipsProcessed:int = -1;
		private var errorText:String;
		
		public function FileUploadStatus()
		{
		}

		public function setReference(file:FileReference):void
		{
			ref = file;
		}
		
		public function setResponseXML(xml:XML):void
		{
			response = xml;	
		}
		
		public function getReference():FileReference
		{
			return ref;	
		}
		
		public function setEntitiesProcessed(num:int):void
		{
			entitiesProcessed = num;	
		}
		
		public function setRelationshipsProcessed(num:int):void
		{
			relationshipsProcessed = num;
		}
		
		public function getEntitiesProcessed():int{
			return entitiesProcessed;
		}

		public function getRelationshipsProcessed():int
		{
			return relationshipsProcessed;
		}

		public function getFilename():String
		{
			if(ref != null)
			{
				return ref.name
			}
			else
			{
				return "null"
			}
		}
		
		public function getErrorText():String{
			return errorText;
		}
		
		public function setErrorText(txt:String):void
		{
			errorText = txt;
		}
		
		[Override]
		public function toString():String {
			
			var ret:String = ref.name;			
			return ret;
		}
	}	
}