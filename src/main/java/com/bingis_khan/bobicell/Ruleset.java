package com.bingis_khan.bobicell;

class Ruleset {
	State nextState(Board b) {
		// Dunno if it's the right call, becuase it's a cyclic dependency (kind of)*.
		// *The thing is, we actually don't 'need' ruleset inside Board.
		// It's only there for convenience's sake, because it never changes.
		
		return null;
	}
}
