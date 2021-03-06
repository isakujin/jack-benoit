package com.pokware.jb;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker;
import com.badlogic.gdx.tools.imagepacker.TexturePacker.Settings;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.fbksoft.engine.ActionResolver;
import com.fbksoft.jb.Constants;
import com.fbksoft.jb.JackBenoitApplication;

public class Main {
	public static void main(String[] args) throws IOException {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Jack Benoit: Kakahuet Hunter";
		cfg.useGL20 = false;
		cfg.width = 1280;
		cfg.height = 720;
		Constants.ZOOM_FACTOR = 0.5f;

		// Generate libgdx-friendly assets
		deleteFiles();
		copyTiledMaps();
		processSprites();
		
		new LwjglApplication(new JackBenoitApplication(new ActionResolverDesktop()), cfg);
	}

	public static void deleteFiles() {
		File outputDir = new File("../jb/data/output");
		File[] listFiles = outputDir.listFiles();
		if (listFiles != null && listFiles.length > 0) {				
			for (File file : listFiles) {
				file.delete();
			}
		}
	}
	
	public static void processSprites() {
		TexturePacker2.Settings settings = new TexturePacker2.Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		TexturePacker2.process(settings, "../jb/data/input/sprites", "../jb/data/output", "pack");
	}

	public static void copyTiledMaps() throws IOException {
		File inputDir = new File("../jb/data/input");
		File outputDir = new File("../jb/data/output");

		System.out.println("Copying tiled map to output...");
		File[] listFiles = inputDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("tsx") || pathname.getName().endsWith("tmx") || pathname.getName().endsWith("png"); 
			}
		});
		for (File file : listFiles) {
			Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(outputDir.getAbsolutePath()+"/"+file.getName()), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
}
