package javastrava.api.v3.service;

import java.util.ArrayList;
import java.util.List;

import javastrava.api.v3.service.exception.NotFoundException;
import javastrava.api.v3.service.exception.UnauthorizedException;
import javastrava.api.v3.service.impl.retrofit.ActivityServicesImpl;
import javastrava.config.Strava;
import javastrava.util.Paging;

/**
 * <p>
 * Provides a standard method of handling paging instructions for almost all calls to the Strava API that support paging.
 * </p>
 * 
 * <p>
 * Example - see {@link ActivityServicesImpl#listActivityComments(Integer, Paging)}
 * </p>
 * 
 * @author Dan Shannon
 *
 */
public class PagingHandler {
	/**
	 * <p>
	 * Validates paging instructions and converts them to Strava-compatible paging instructions, then gets the whole lot for you
	 * </p>
	 * 
	 * <p>
	 * The {@link PagingCallback} provides the functionality to get a single page of data from Strava
	 * </p>
	 * 
	 * @param pagingInstruction
	 *            The overarching paging instruction to be managed
	 * @param callback
	 *            An implementation of PagingCallback which actually gets the relevant page of data from the Strava API
	 * @param <T>
	 *            The class of objects which will be returned in the list
	 * @return List of strava objects as per the paging instruction
	 */
	public static <T> List<T> handlePaging(final Paging pagingInstruction, final PagingCallback<T> callback) {
		Strava.validatePagingArguments(pagingInstruction);
		List<T> records = new ArrayList<T>();
		try {
			for (Paging paging : Strava.convertToStravaPaging(pagingInstruction)) {
				List<T> pageOfData = callback.getPageOfData(paging);
				if (pageOfData.size() == 0) {
					break;
				}
				pageOfData = Strava.ignoreLastN(pageOfData, paging.getIgnoreLastN());
				pageOfData = Strava.ignoreFirstN(pageOfData, paging.getIgnoreFirstN());
				records.addAll(pageOfData);
			}
		} catch (NotFoundException e) {
			return null;
		} catch (UnauthorizedException e) {
			return new ArrayList<T>();
		}
		return records;

	}

	/**
	 * <p>
	 * Returns ALL the data from a Strava service that would normally only return a page of data, by simply getting pages 1..n until there's no more data to retrieve</p>
	 * </p>
	 * 
	 * <p>
	 * USE WITH CAUTION! THIS WILL VERY RAPIDLY EAT THROUGH YOUR STRAVA QUOTA!
	 * </p>
	 * 
	 * <p>
	 * The {@link PagingCallback} provides the method to return a single page of data
	 * </p>
	 * 
	 * @param callback The callback function that returns one page of data
	 * @param <T> the parameterised type of list to be returned
	 * @return The list containing all the records
	 */
	public static <T> List<T> handleListAll(final PagingCallback<T> callback) {
		boolean loop = true;
		List<T> records = new ArrayList<T>();
		int page = 0;
		while (loop) {
			page++;
			List<T> currentPage;
			try {
				currentPage = callback.getPageOfData(new Paging(page, Strava.MAX_PAGE_SIZE));
			} catch (NotFoundException e) {
				return null;
			} catch (UnauthorizedException e) {
				return new ArrayList<T>();
			}
			if (currentPage == null) {
				return null; // Activity doesn't exist
			}
			records.addAll(currentPage);
			if (currentPage.size() < Strava.MAX_PAGE_SIZE) {
				loop = false;
			}
		}
		return records;

	}
}
