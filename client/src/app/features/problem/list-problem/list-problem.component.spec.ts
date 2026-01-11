import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListProblemComponent } from './list-problem.component';

describe('ListProblemComponent', () => {
  let component: ListProblemComponent;
  let fixture: ComponentFixture<ListProblemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListProblemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListProblemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
