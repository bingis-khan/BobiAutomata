package com.bingis_khan.bobicell;

import java.util.function.Supplier;

class Functions {
	class FunctionBuilder {
		private final Functions caller;
		
		
		FunctionBuilder(Functions caller) {
			this.caller = caller;
		}
		
		
		public FunctionBuilder then(String identifier) {
			return this;
		}
		
		public FunctionBuilder thenArgument(TokenType type) {
			return this;
		}
		
		public void whichExecutesAs(Condition pr) {
			
		}
	}
	
	
	public FunctionBuilder newFunction(String name) {
		return new FunctionBuilder(this);
	}
	
	
	public Condition call(Parser parser) {
		return null;
	}
}
