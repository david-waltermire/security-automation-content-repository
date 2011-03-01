package org.scapdev.content.repository.explorer
{
	import flash.net.FileReference;
	
	import mx.controls.Alert;
	
	/**
	 * Extends a FileReference so that we can override toString
	 */
	public class MyFileReference
	{
		private var ref:FileReference;
		private var response:XML;
		
		public function MyFileReference(other:FileReference)
		{
			ref = other;
		}
		
		public function getReference():FileReference
		{
			return ref;	
		}
		
		[Override]
		public function toString():String {
			
			var ret:String = ref.name;			
			return ret;
		}
	}	
}