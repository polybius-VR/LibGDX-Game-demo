package com.project.editor.util;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.image.ImageView;

public class EditorGrid {
	private static final AtomicInteger count = new AtomicInteger(0); 
	private final int id;
	private ImageView image;
	private Integer type;
	
	public EditorGrid(){
		id = count.incrementAndGet(); 
		image = null;
		type = 0;
	}
	
	public EditorGrid(ImageView image, Integer type){
		id = count.incrementAndGet(); 
		this.image = image;
		this.type = type;
	}
	
	public ImageView getImage() {
		return image;
	}
	public void setImage(ImageView image) {
		this.image = image;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	public int getId(){
		return this.id;
	}
}
