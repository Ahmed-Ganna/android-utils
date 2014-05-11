package nl.changer.android.opensource;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class ViewUtils {

	/***
	 * Show live character counter for the number of characters typed in the parameter {@link EditText}
	 * 
	 * @param editTextView Characters to count from
	 * @param textCounterView {@link TextView} to show live character count in
	 * @param maxCharCount Max characters that can be typed in into the parameter edittext
	 * @param countdown if true, only the remaining of the max character count will be displayed. If false, 
	 * current character count as well as max character count will be displayed in the UI.
	 ****/
	public static void setLiveCharCounter(EditText editTextView, final TextView textCounterView, final int maxCharCount, final boolean countdown) {
		
		if(editTextView == null)
			throw new NullPointerException("View to count text characters on cannot be null");
		
		if(textCounterView == null)
			throw new NullPointerException("View to display count cannot be null");
		
		// initialize the TextView initial state
		if(countdown)
			textCounterView.setText(String.valueOf(maxCharCount));
		else
			textCounterView.setText(String.valueOf("0 / " + maxCharCount));
		
		// initialize the edittext
		setMaxLength(editTextView, maxCharCount);
		
		editTextView.addTextChangedListener(new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	
	        }
	
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	
	        	if(countdown) {
	        		// show only the remaining number of characters
	        		int charsLeft = maxCharCount - s.length();
		        	
		        	if( charsLeft >= 0 ) {
		        		textCounterView.setText(String.valueOf(charsLeft));
		        	}
	        	} else {
	        		// show number of chars / maxChars in the UI
	        		textCounterView.setText(s.length() + " / " + maxCharCount);
	        	}
	        	
	        }
	
	        public void afterTextChanged(Editable s) {
	        	
	        }
		});
	}

	public static void setMaxLength(TextView textView, int maxLength) {
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		textView.setFilters(fArray);
	}
}
