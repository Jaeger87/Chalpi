package bot;

public class Triple<L,C,R> {

	  private final L left;
	  private final C center;
	  private final R right;

	  public Triple(L left, C center, R right) {
	    this.left = left;
	    this.center = center;
	    this.right = right;
	    
	  }

	  public L getLeft() { return left; }
	  public C getCenter() { return center; }
	  public R getRight() { return right; }

	  @Override
	  public int hashCode() { return left.hashCode() ^ right.hashCode() ^ center.hashCode(); }

	  @Override
	  public boolean equals(Object o) {
	    if (!(o instanceof Triple)) return false;
	    Triple tripleo = (Triple) o;
	    return this.left.equals(tripleo.getLeft()) &&
	           this.right.equals(tripleo.getRight()) &&
	           this.center.equals(tripleo.getCenter());
	  }

	}