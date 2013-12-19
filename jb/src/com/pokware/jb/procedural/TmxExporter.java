package com.pokware.jb.procedural;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class TmxExporter {

	private TiledMap tiledMap;

	public TmxExporter(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	public void export(File output)  {
		Document doc = getNewDocument();
		Element mapElement = doc.createElement("map");
		mapElement.setAttribute("version", "1.0");
		mapElement.setAttribute("orientation", "orthogonal");
		TiledMapTileLayer firstLayer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		mapElement.setAttribute("width", String.valueOf(firstLayer.getWidth()));
		mapElement.setAttribute("height", String.valueOf(firstLayer.getHeight()));
		mapElement.setAttribute("tilewidth", String.valueOf((int)firstLayer.getTileWidth()));
		mapElement.setAttribute("tileheight", String.valueOf((int)firstLayer.getTileHeight()));
		doc.appendChild(mapElement);
		
		Element tileSetElement = doc.createElement("tileset");
		tileSetElement.setAttribute("firstfid", "1");
		tileSetElement.setAttribute("source", "general.tsx");		
		mapElement.appendChild(tileSetElement);

		MapLayers layers = tiledMap.getLayers();
		for (MapLayer mapLayer : layers) {			
			Element layerElement = createLayer(doc, (TiledMapTileLayer)mapLayer);
			mapElement.appendChild(layerElement);
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {		
			e.printStackTrace();
			return;
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(output);

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			return;
		}

	}

	private Element createLayer(Document doc, TiledMapTileLayer mapLayer) {
		Element layerElement = doc.createElement("layer");
		layerElement.setAttribute("name", mapLayer.getName());
		layerElement.setAttribute("width", String.valueOf(mapLayer.getWidth()));
		layerElement.setAttribute("height", String.valueOf(mapLayer.getHeight()));
		
		Element dataElement = doc.createElement("data");
		layerElement.appendChild(dataElement);
		dataElement.setAttribute("encoding", "csv");
		
		StringBuffer layerData = new StringBuffer();
				
		for(int y=mapLayer.getHeight()-1; y>=0; y--) {
			for(int x=0; x<mapLayer.getWidth(); x++) {
				Cell cell = mapLayer.getCell(x, y);
				if (cell!=null) {
					layerData.append((cell.getTile().getId()-1) + ",");
				}
				else {
					layerData.append("0,");
				}
			}
			layerData.append("\n");
		}
		
		Text dataNode = doc.createTextNode(layerData.substring(0, layerData.length() - 2));
		dataElement.appendChild(dataNode);
		
		return layerElement;
	}

	private Document getNewDocument() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {		
			e.printStackTrace();
			return null;
		}

		// root elements
		Document doc = docBuilder.newDocument();
		return doc;
	}

}
