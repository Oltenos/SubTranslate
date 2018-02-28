package com.model;

import java.util.ArrayList;
import java.util.List;

import com.beans.Language;
import com.beans.SubtitleLine;
import com.beans.Subtitles;
import com.dao.DaoException;
import com.dao.DaoFactory;

public class Model {
	/**
	 * Message en cas d'erreur
	 */
	private String errorMessage = "";
	/**
	 * Sous titres originaux
	 */
	private Subtitles subtitlesOriginal;
	/**
	 * Sous titre traduit
	 */
	private Subtitles subtitlesDestination;

	/**
	 * instance unique de Model
	 */
	private static Model instance = new Model();

	/**
	 * Constructeur privé
	 */
	private Model() {

	}
	/**
	 * Acces à l'instance unique de Model
	 * 
	 * @return instance de Model
	 */
	public static Model getInstance() {
		if (instance == null)
			instance = new Model();
		return instance;
	}

	/**
	 * Retourne la liste des noms des langues
	 * 
	 * @return liste des noms des langues
	 */
	public String[] getLanguagesNames() {
		List<Language> list = null;
		try {
			list = DaoFactory.getDaoLanguage().list();
		} catch (DaoException e) {
			setError(e.getMessage());
			e.printStackTrace();
		}
		String[] result = new String[list.size()];
		int i = 0;
		for (Language language : list) {
			result[i] = language.getName();
			i++;
		}
		return result;
	}

	/**
	 * Retourne l'objet Language à partir de son nom
	 * 
	 * @param languagesNames nom de la langue
	 * @return objet language correspondant
	 */
	public Language getLanguages(String languagesNames) {
		List<Language> list = null;
		try {
			list = DaoFactory.getDaoLanguage().list();
		} catch (DaoException e) {
			setError(e.getMessage());
			e.printStackTrace();
		}
		Language result = null;
		for (Language language : list) {
			if (language.getName().equalsIgnoreCase(languagesNames)) {
				result = language;
			}
		}
		return result;
	}

	/**
	 * Retourne la liste de tous les noms des sous titres sous le format titre.abrevationlangue
	 * 
	 * @return noms des sous titres
	 */
	public String[] getSubtitlesNames() {
		List<Subtitles> list = null;
		try {
			list = DaoFactory.getDaoSubtitles().list();
		} catch (DaoException e) {
			setError(e.getMessage());
			e.printStackTrace();
		}
		String[] result = new String[list.size()];
		int i = 0;
		for (Subtitles subtitles : list) {
			result[i] = subtitles.getTitle() + "." + subtitles.getLanguage().getAbreviation();
			i++;
		}
		return result;
	}

	/**
	 * Retourne la liste de tous les noms des sous titres originaux sous le format titre.abrevationlangue
	 * 
	 * @return noms des sous titres
	 */
	public String[] getSubtitlesOriginalsNames() {
		List<Subtitles> list = null;
		try {
			list = DaoFactory.getDaoSubtitles().list();
		} catch (DaoException e) {
			setError(e.getMessage());
			e.printStackTrace();
		}
		
		List<Subtitles> subtitlesOriginals = new ArrayList<Subtitles>();
		for (Subtitles subtitles : list) {
			if(subtitles.getIdOriginal()==0) {
				subtitlesOriginals.add(subtitles);
			}
		}
		String[] result = new String[subtitlesOriginals.size()];
		int i = 0;
		for (Subtitles subtitles : subtitlesOriginals) {
			result[i] = subtitles.getTitle() + "." + subtitles.getLanguage().getAbreviation();
			i++;
		}
		return result;
	}
	
	/**
	 * Retourne le sous titres à partir de son titre et de sa langue
	 * retourne null si il n'existe pas dans la base de données
	 * 
	 * @param title titre
	 * @param language langue
	 * @return sous titre correspondant
	 */
	private Subtitles getSubtitles(String title, String language) {
		List<Subtitles> list = null;
		try {
			list = DaoFactory.getDaoSubtitles().list();
		} catch (DaoException e) {
			setError(e.getMessage());
			e.printStackTrace();
		}
		Subtitles result = null;
		for (Subtitles subtitles : list) {
			if (subtitles.getTitle().equalsIgnoreCase(title) && subtitles.getLanguage().getAbreviation().equalsIgnoreCase(language)) {
				result = subtitles;
			}
		}
		return result;
	}

	/**
	 * setter de subtitlesOriginal à partir de son titre et de sa langue (recherche dans la base de donné les sous titres correspondant)
	 * 
	 * @param title titre
	 * @param language langue abréviation de la langue
	 */
	public void setSubtitlesOriginal(String title, String language) {
		Subtitles sub = this.getSubtitles(title, language);
		if(sub != null)
			this.subtitlesOriginal = this.getSubtitles(title, language);
		else
			this.setError("Erreur lors du chargement des sous titres");
	}

	/**
	 * setter de subtitlesDestination à partir de son titre et de sa langue: -
	 * recherche dans la base de donné les sous titres correspondant si existant -
	 * sinon : crée l'objet avec le même nombre de ligne (vide) que le
	 * subtitlesOriginal et les mêmes parametres
	 * 
	 * @param title titre
	 * @param language nom de la langue
	 */
	public void setSubtitlesDestination(String title, String language) {
		Subtitles subtitles = this.getSubtitles(title, this.getLanguages(language).getAbreviation());
		if (subtitles == null) {
			this.subtitlesDestination = new Subtitles();
			this.subtitlesDestination.setLanguage(this.getLanguages(language));
			this.subtitlesDestination.setIdOriginal(this.subtitlesOriginal.getId());
			this.subtitlesDestination.setTitle(title);
			List<SubtitleLine> subtitleLinesDestination = new ArrayList<SubtitleLine>();
			List<SubtitleLine> subtitleLinesOriginal = this.subtitlesOriginal.getsubTitleLines();
			for (SubtitleLine subtitleLineOriginal : subtitleLinesOriginal) {
				SubtitleLine subLine = new SubtitleLine();
				subLine.setId(subtitleLineOriginal.getId());
				subLine.settStart(subtitleLineOriginal.gettStart());
				subLine.settEnd(subtitleLineOriginal.gettEnd());
				subLine.setLine1("");
				subLine.setLine2("");
				subtitleLinesDestination.add(subLine);
			}
			this.subtitlesDestination.setsubTitleLines(subtitleLinesDestination);
			try {
				DaoFactory.getDaoSubtitles().add(subtitlesDestination);
			} catch (DaoException e) {
				setError(e.getMessage());
				e.printStackTrace();
			}
			this.subtitlesDestination = this.getSubtitles(title, this.getLanguages(language).getAbreviation());
		} else {
			this.subtitlesDestination = subtitles;
		}
	}
	
	/**
	 * getter de subtitlesOriginal
	 * 
	 * @return subtitlesOriginal
	 */
	public Subtitles getSubtitlesOriginal() {
		return subtitlesOriginal;
	}
	
	/**
	 * getter de subtitlesDestination
	 * 
	 * @return subtitlesDestination
	 */
	public Subtitles getsubtitlesDestination() {
		return subtitlesDestination;
	}

	public void setError(String message) {
		System.out.println(message);
		this.errorMessage = message;
	}
	
	public String getError() {
		return this.errorMessage;
	}
	
	/**
	 * set subtitlesDestination et enregistre les modification dans la base de données
	 * @param subtitlesDest
	 */
	public void setSubtitlesDestination(Subtitles subtitlesDest) {
		this.subtitlesDestination=subtitlesDest;
		try {
			DaoFactory.getDaoSubtitles().update(this.subtitlesDestination);
		} catch (DaoException e) {
			this.setError("Erreur lors de l'enregistrement des modifications dans la base de données");
			e.printStackTrace();
		}
	}

}