import java.util.Stack;

public class GameLoop implements Runnable {
	private boolean hosting;
	private GameRenderer gameRenderer;
	private GameManager gameManager;
	private Stack<GameManager> gameManagerStack;
	
	public GameLoop(GameRenderer gameRenderer, GameManager gameManager, Stack<GameManager> gameManagerStack) {
		this.gameRenderer = gameRenderer;
		this.gameManager = gameManager;
		this.gameManagerStack = gameManagerStack;
		hosting = false;
	}
	
	public GameLoop(GameRenderer gameRenderer, GameManager gameManager) {
		this.gameRenderer = gameRenderer;
		this.gameManager = gameManager;
		hosting = true;
	}
	
	@Override
	public void run() {
        //Clear last frame
        gameRenderer.clear();
        //Draw cells
        gameRenderer.renderCells();
        //Draw snake
        gameRenderer.renderSnakes(gameManager.getSnakes());
        //Draw apple
        gameRenderer.renderApple(gameManager.getApple());
        //Draw scores
        gameRenderer.renderScores(gameManager.getSnakes());

//        if (hosting)
        	gameManager.manage();
        
        if (!hosting)
        	if (!gameManagerStack.isEmpty())
        		gameManager = gameManagerStack.pop();
        
        gameRenderer.show();
	}
}