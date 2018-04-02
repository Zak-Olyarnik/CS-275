package com.example.l3;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity  {

	boolean xTurn = true;		// keeps track of whose turn it is
	int numTurns = 0;			// if nine turns have passed, it's a draw
	String winner = "";			// keeps track of whether or not a winner has been declared
									// for endgame analysis
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// meaningful code here
		
		// maps each button from the graphical view to a usable variable
		final Button button01 = (Button) findViewById(R.id.Button01);
		final Button button02 = (Button) findViewById(R.id.Button02);
		final Button button03 = (Button) findViewById(R.id.Button03);
		final Button button04 = (Button) findViewById(R.id.Button04);
		final Button button05 = (Button) findViewById(R.id.Button05);
		final Button button06 = (Button) findViewById(R.id.Button06);
		final Button button07 = (Button) findViewById(R.id.Button07);
		final Button button08 = (Button) findViewById(R.id.Button08);
		final Button button09 = (Button) findViewById(R.id.Button09);
		final Button buttonNew = (Button) findViewById(R.id.ButtonNew);
		final TextView message = (TextView) findViewById(R.id.TextView01);
		
		// starts new game if that button is clicked
		OnClickListener newgame = new OnClickListener(){
			public void onClick(View v){
				numTurns = 0;
				button01.setText("");
				button02.setText("");
				button03.setText("");
				button04.setText("");
				button05.setText("");
				button06.setText("");
				button07.setText("");
				button08.setText("");
				button09.setText("");
				message.setText("X's Turn");
				xTurn = true;
				winner = "";
			}
		};
		
		// determines if a legal move was made and checks for win
		OnClickListener move = new OnClickListener(){
			public void onClick(View v){
				
				if(winner.equals("")){
					// determines which button was clicked
					switch(v.getId()){
						case R.id.Button01:
							if(button01.getText().toString().equals("")){
								if(xTurn){
									button01.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button01.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
						case R.id.Button02:
							if(button02.getText().toString().equals("")){
								if(xTurn){
									button02.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button02.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
						case R.id.Button03:
							if(button03.getText().toString().equals("")){
								if(xTurn){
									button03.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button03.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
						case R.id.Button04:
							if(button04.getText().toString().equals("")){
								if(xTurn){
									button04.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button04.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
						case R.id.Button05:
							if(button05.getText().toString().equals("")){
								if(xTurn){
									button05.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button05.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
						case R.id.Button06:
							if(button06.getText().toString().equals("")){
								if(xTurn){
									button06.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button06.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
						case R.id.Button07:
							if(button07.getText().toString().equals("")){
								if(xTurn){
									button07.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button07.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
						case R.id.Button08:
							if(button08.getText().toString().equals("")){
								if(xTurn){
									button08.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button08.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
						case R.id.Button09:
							if(button09.getText().toString().equals("")){
								if(xTurn){
									button09.setText("X");
									numTurns = numTurns + 1;
									xTurn = false;
								}else{
									button09.setText("O");
									numTurns = numTurns + 1;
									xTurn = true;
								}
							}
							break;
					}
				
					// notifies whose turn it is
					if(xTurn){
						message.setText("X's Turn");
					}else{
						message.setText("O's Turn");
					}
				
					// check for win and print message
					if(numTurns > 4){
						winner = check();
						if(!(winner.equals(""))){
							message.setText(winner + " Wins!");
						}else if (numTurns == 9){
							message.setText("It's a draw!");
						}
					}
				}
			}
			
			// checks for win
			public String check(){
				// row check, X
				if(button01.getText().toString().equals("X") && 
						button02.getText().toString().equals("X") &&
						button03.getText().toString().equals("X")){
					return "X";
				}else if (button04.getText().toString().equals("X") && 
						button05.getText().toString().equals("X") &&
						button06.getText().toString().equals("X")){
					return "X";
				}else if (button07.getText().toString().equals("X") && 
						button08.getText().toString().equals("X") &&
						button09.getText().toString().equals("X")){
					return "X";
				}
				// column check, X
				if(button01.getText().toString().equals("X") && 
						button04.getText().toString().equals("X") &&
						button07.getText().toString().equals("X")){
					return "X";
				}else if (button02.getText().toString().equals("X") && 
						button05.getText().toString().equals("X") &&
						button08.getText().toString().equals("X")){
					return "X";
				}else if (button03.getText().toString().equals("X") && 
						button06.getText().toString().equals("X") &&
						button09.getText().toString().equals("X")){
					return "X";
				}
				// diagonal check, X
				if(button01.getText().toString().equals("X") && 
						button05.getText().toString().equals("X") &&
						button09.getText().toString().equals("X")){
					return "X";
				}else if (button03.getText().toString().equals("X") && 
						button05.getText().toString().equals("X") &&
						button07.getText().toString().equals("X")){
					return "X";
				}
				
				// row check, O
				if(button01.getText().toString().equals("O") && 
						button02.getText().toString().equals("O") &&
						button03.getText().toString().equals("O")){
					return "O";
				}else if (button04.getText().toString().equals("O") && 
						button05.getText().toString().equals("O") &&
						button06.getText().toString().equals("O")){
					return "O";
				}else if (button07.getText().toString().equals("O") && 
						button08.getText().toString().equals("O") &&
						button09.getText().toString().equals("O")){
					return "O";
				}
				// column check, O
				if(button01.getText().toString().equals("O") && 
						button04.getText().toString().equals("O") &&
						button07.getText().toString().equals("O")){
					return "O";
				}else if (button02.getText().toString().equals("O") && 
						button05.getText().toString().equals("O") &&
						button08.getText().toString().equals("O")){
					return "O";
				}else if (button03.getText().toString().equals("O") && 
						button06.getText().toString().equals("O") &&
						button09.getText().toString().equals("O")){
					return "O";
				}
				// diagonal check, O
				if(button01.getText().toString().equals("O") && 
						button05.getText().toString().equals("O") &&
						button09.getText().toString().equals("O")){
					return "O";
				}else if (button03.getText().toString().equals("O") && 
						button05.getText().toString().equals("O") &&
						button07.getText().toString().equals("O")){
					return "O";
				}
				
				// no winner yet
				return "";
			}
		};

		// sets the buttons to listen for 
		button01.setOnClickListener(move);
		button02.setOnClickListener(move);
		button03.setOnClickListener(move);
		button04.setOnClickListener(move);
		button05.setOnClickListener(move);
		button06.setOnClickListener(move);
		button07.setOnClickListener(move);
		button08.setOnClickListener(move);
		button09.setOnClickListener(move);
		buttonNew.setOnClickListener(newgame);
	}

	
	
	// from here on was auto-generated
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
