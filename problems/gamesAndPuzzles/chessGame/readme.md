Functional Requirement:
1-> support a standart 8*8 chess board with all piece type (KING, QUEEN, PAWN, KNIGHT, ROOK, BISHOP)
2-> Enforce peice-specific movement rules, including path clearance for sliding pieces
3-> validate that moves does not leaves the moving player's king in check.
4-> support special moves: castling (kingside and queenside), en pessant, and pawn promotion
5-> track move history for entire game
6-> support player resignation to end the game immediately
7-> enforece turn based play, alternating betn white and black
8-> initialize the board with piece in their standar starting posn


Non-Function Requirement:
1-> piece logic movement should be extensible without modifying existing class
2-> Design should follow Object oriented principles
3-> The code should be clean with meaningfull variables
4-> System should be modular to test each individual components in isolation
5-> The design shold be extensible for future enhancement like undo/redo and move retentation
