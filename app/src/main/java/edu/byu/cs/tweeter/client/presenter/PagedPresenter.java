package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {
    private static final int PAGE_SIZE = 10;

    private T lastItem;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public interface PagedView<U> extends Presenter.View {
        void setLoadingFooter(boolean value);
        void addMoreItems(List<U> items);
        void displayInfo(User user);
    }

    public PagedPresenter(View view) {
        super(view);
    }

    public void loadUsersItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            ((PagedView)view).setLoadingFooter(isLoading);
            loadItems(user, PAGE_SIZE, lastItem);
        }
    }

    public void addItemsToView(List<T> items, boolean hasMorePages) {
        setLoading(false);
        lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
        setHasMorePages(hasMorePages);
        ((PagedView)view).addMoreItems(items);
    }

    protected abstract void loadItems(User user, int PAGE_SIZE, T lastItem);

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        ((PagedView)view).setLoadingFooter(isLoading);
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }
}
