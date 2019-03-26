package opengl;

public class ObjTexture {

	private int textureID;
	private float shineDamper = 0.2f;
	private float reflectivity = 0.9f;

	public ObjTexture(int id) {
		textureID = id;
	}


	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public int getID() {
		return textureID;
	}
}
