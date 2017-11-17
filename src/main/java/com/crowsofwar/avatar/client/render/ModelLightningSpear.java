// Date: 11/5/2017 8:30:38 AM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX






package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityLightningSpear;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelLightningSpear extends ModelBase
{
  //fields
    ModelRenderer Shape1;
    ModelRenderer Shape2;
  
  public ModelLightningSpear()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Shape1 = new ModelRenderer(this, 10, 0);
      Shape1.addBox(0F, 0F, 0F, 3, 3, 8);
	  Shape1.setRotationPoint(-1F, 18F, -4F);
      Shape1.setTextureSize(textureWidth, textureHeight);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
      Shape2 = new ModelRenderer(this, 0, 0);
      Shape2.addBox(0F, 0F, 0F, 1, 1, 6);
      Shape2.setRotationPoint(0F, 19F, -3F);
      Shape2.setTextureSize(textureWidth, textureHeight);
      Shape2.mirror = true;
      setRotation(Shape2, 0F, 0F, 0F);

	          this.Shape1 = new ModelRenderer(this, 4, 4);
	          this.Shape1.setRotationPoint(-6.2F, 4.0F, -0.8F);
	          this.Shape1.addBox(0.0F, 0.0F, 0.0F, 13, 2, 2, 0.0F);
	  Shape1.setTextureOffset(10, 0);


  }

	@Override
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    Shape1.render(f5);
    Shape2.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, EntityLightningSpear entity)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
