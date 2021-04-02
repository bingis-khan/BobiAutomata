package com.bingis_khan.bobicell;

import java.awt.Point;

class Ruleset {
	State nextState(Board b, int x, int y) {
		// Dunno if it's the right call, becuase it's a cyclic dependency (kind of)*.
		// *The thing is, we actually don't 'need' ruleset inside Board.
		// It's only there for convenience's sake, because it never changes.
		
		return null;
	}
}
