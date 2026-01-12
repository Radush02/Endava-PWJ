import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestcaseCreateComponent } from './testcase-create.component';

describe('TestcaseCreateComponent', () => {
  let component: TestcaseCreateComponent;
  let fixture: ComponentFixture<TestcaseCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestcaseCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TestcaseCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
